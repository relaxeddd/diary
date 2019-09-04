package relaxeddd.simplediary.model.http

import android.system.ErrnoException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.OkHttpClient
import okhttp3.internal.http2.StreamResetException
import relaxeddd.simplediary.BuildConfig
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
import java.util.concurrent.CountDownLatch

class ApiHelper {

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
    suspend fun requestInit(pushToken: String) : InitResult? {
        if (!isNetworkAvailable() || userTokenId?.isNotEmpty() != true || firebaseUserId.isEmpty() || firebaseUserEmail.isEmpty()) {
            return InitResult(Result(RESULT_ERROR_INTERNET), User())
        }

        return executeRequest( suspend {
            api.requestInit(TOKEN_PREFIX + userTokenId, firebaseUserId, BuildConfig.VERSION_CODE, pushToken, firebaseUserEmail)
        }, InitResult(Result(RESULT_ERROR_INTERNET), User()))
    }

    suspend fun requestUpdateUser() : UpdateUserResult? {
        if (!isNetworkAvailable() || userTokenId?.isNotEmpty() != true || firebaseUserId.isEmpty()) {
            return UpdateUserResult(Result(RESULT_ERROR_INTERNET), User())
        }

        return executeRequest( suspend {
            api.requestUpdateUser(TOKEN_PREFIX + userTokenId, firebaseUserId)
        }, UpdateUserResult(Result(RESULT_ERROR_INTERNET), User()))
    }

    suspend fun requestSendFeedback(feedback: String) : Result? {
        if (!isNetworkAvailable() || userTokenId?.isNotEmpty() != true || firebaseUserId.isEmpty()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        return executeRequest( suspend {
            api.requestSendFeedback(TOKEN_PREFIX + userTokenId, firebaseUserId, feedback)
        }, Result(RESULT_ERROR_INTERNET))
    }

    suspend fun requestVerifyPurchase(purchaseTokenId: String, signature: String, originalJson: String,
                                      itemType: String) : PurchaseResult? {
        if (!isNetworkAvailable() || userTokenId?.isNotEmpty() != true || firebaseUserId.isEmpty()) {
            return PurchaseResult(Result(RESULT_ERROR_INTERNET))
        }

        return executeRequest( suspend {
            api.requestVerifyPurchase(TOKEN_PREFIX + userTokenId, firebaseUserId, purchaseTokenId, signature,
                originalJson, itemType)
        }, PurchaseResult(Result(RESULT_ERROR_INTERNET)))
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
    private suspend fun <T> executeRequest(request: suspend () -> T, defaultAnswer: T) = try {
        request()
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