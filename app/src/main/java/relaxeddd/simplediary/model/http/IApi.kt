package relaxeddd.simplediary.model.http

import relaxeddd.simplediary.common.*
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface IApi {

    @GET(FUNC_INIT)
    suspend fun requestInit(@Header("Authorization") idToken: String,
                            @Query("userId") userId: String,
                            @Query("appVersion") appVersion: Int,
                            @Query("pushToken") pushToken: String,
                            @Query("email") email: String) : ServerAnswer<InitContent>?

    @GET(FUNC_VERIFY_PURCHASE)
    suspend fun requestVerifyPurchase(@Header("Authorization") idToken: String,
                                      @Query("userId") userId: String,
                                      @Query("purchaseTokenId") purchaseTokenId: String,
                                      @Query("signature") signature: String,
                                      @Query("originalJson") originalJson: String,
                                      @Query("itemType") itemType: String) : ServerAnswer<PurchaseContent>?

    @GET(FUNC_SEND_FEEDBACK)
    suspend fun requestSendFeedback(@Header("Authorization") idToken: String,
                                    @Query("userId") userId: String,
                                    @Query("message") message: String) : ServerAnswer<Void>?

    @GET(FUNC_UPDATE_USER)
    suspend fun requestUpdateUser(@Header("Authorization") idToken: String,
                                  @Query("userId") userId: String) : ServerAnswer<UserContent>?
}