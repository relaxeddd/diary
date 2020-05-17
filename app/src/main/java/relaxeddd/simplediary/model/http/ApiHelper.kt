package relaxeddd.simplediary.model.http

import android.system.ErrnoException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.OkHttpClient
import okhttp3.internal.http2.StreamResetException
import relaxeddd.simplediary.common.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLHandshakeException
import com.google.gson.GsonBuilder
import relaxeddd.simplediary.model.NetworkHelper
import java.util.concurrent.CountDownLatch

class ApiHelper(private val networkHelper: NetworkHelper) {

    companion object {

        private const val TOKEN_PREFIX = "Bearer "
        private const val LATCH_TIMEOUT = 30L
    }

    private val api: IApi

    private val firebaseUser get() = FirebaseAuth.getInstance().currentUser
    private val firebaseUserId get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val firebaseUserEmail get() = FirebaseAuth.getInstance().currentUser?.email ?: ""
    private var userTokenId: String? = null

    init {
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(URL_FIREBASE)
            .build()
        api = retrofit.create(IApi::class.java)
    }

    //------------------------------------------------------------------------------------------------------------------
    suspend fun requestInit(pushToken: String) : ServerAnswer<InitContent> {
        return ServerAnswer(RESULT_OK, content = InitContent(User("1231231", "abc@yandex.ru")))

        /*return executeRequest( suspend {
            api.requestInit(TOKEN_PREFIX + userTokenId, firebaseUserId, BuildConfig.VERSION_CODE, pushToken, firebaseUserEmail)
        })*/
    }

    suspend fun requestUpdateUser() : ServerAnswer<UserContent> {
        return executeRequest( suspend {
            api.requestUpdateUser(TOKEN_PREFIX + userTokenId, firebaseUserId)
        })
    }

    suspend fun requestSendFeedback(feedback: String) : ServerAnswer<Void> {
        return executeRequest( suspend {
            api.requestSendFeedback(TOKEN_PREFIX + userTokenId, firebaseUserId, feedback)
        })
    }

    suspend fun requestVerifyPurchase(purchaseTokenId: String, signature: String, originalJson: String,
                                      itemType: String) : ServerAnswer<PurchaseContent> {
        return executeRequest( suspend {
            api.requestVerifyPurchase(TOKEN_PREFIX + userTokenId, firebaseUserId, purchaseTokenId, signature,
                originalJson, itemType)
        })
    }

    //------------------------------------------------------------------------------------------------------------------
    fun initUserTokenId() : Resource<String> {
        var answer: Resource<String> = Resource(errorStr = ERROR_NOT_AUTHORIZED)
        val latch = CountDownLatch(1)
        val firebaseUser = firebaseUser

        if (firebaseUser != null) {
            firebaseUser.getIdToken(false).addOnCompleteListener {
                if (it.isSuccessful) {
                    userTokenId = it.result?.token ?: ""
                    answer = Resource(status = RESULT_OK, value = userTokenId)
                } else {
                    answer = Resource(errorStr = ERROR_NOT_AUTHORIZED)
                }
                latch.countDown()
            }
        } else {
            latch.countDown()
        }
        latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS)

        return answer
    }

    fun initPushTokenId() : Resource<String> {
        var answer: Resource<String> = Resource(errorStr = ERROR_NOT_AUTHORIZED)
        val latch = CountDownLatch(1)

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            answer = if (it.isSuccessful) {
                Resource(status = RESULT_OK, value = it.result?.token ?: "")
            } else {
                Resource(errorStr = ERROR_NOT_AUTHORIZED)
            }
            latch.countDown()
        }
        latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS)

        return answer
    }

    //------------------------------------------------------------------------------------------------------------------
    private suspend fun <T> executeRequest(request: suspend () -> ServerAnswer<T>?,
                                           defaultAnswer: ServerAnswer<T> = ServerAnswer(RESULT_ERROR_INTERNET)) : ServerAnswer<T> {
        if (!networkHelper.isNetworkAvailable() || userTokenId?.isNotEmpty() != true || firebaseUserId.isEmpty()) {
            return ServerAnswer(RESULT_ERROR_INTERNET)
        }

        return try {
            request() ?: ServerAnswer(RESULT_UNDEFINED)
        } catch (e: UnknownHostException) {
            defaultAnswer
        } catch (e: SocketTimeoutException) {
            defaultAnswer
        } catch (e: StreamResetException) {
            defaultAnswer
        } catch (e: HttpException) {
            defaultAnswer
        } catch (e: ConnectException) {
            defaultAnswer
        } catch (e: SSLHandshakeException) {
            defaultAnswer
        } catch (e: ErrnoException) {
            defaultAnswer
        }
    }
}