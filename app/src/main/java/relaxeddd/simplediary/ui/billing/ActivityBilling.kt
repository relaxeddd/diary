package relaxeddd.simplediary.ui.billing

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.ui.ActivityBase
import relaxeddd.simplediary.ui.ViewModelBase

abstract class ActivityBilling<VM : ViewModelBase, B : ViewDataBinding> : ActivityBase<VM, B>(), PurchasesUpdatedListener {

    companion object {
        @BillingClient.SkuType private const val PRODUCT_1 = "product_1"
        @BillingClient.SkuType private const val PRODUCT_2 = "product_2"
        @BillingClient.SkuType private const val PRODUCT_3 = "product_3"

        var listSkuDetails: List<SkuDetails> = ArrayList()
    }

    private var billingClient: BillingClient? = null
    private var isBillingServiceConnected = false
    private var attemptConnect = 0
    var isBillingInit = false

    //------------------------------------------------------------------------------------------------------------------
    override fun onNavigationEvent(type: EventType, args: Bundle?) {
        when (type) {
            EventType.INIT_BILLING -> {
                if (isActivityResumed && !isBillingInit) {
                    initBilling(object: ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            if (result) isBillingInit = true
                        }
                    })
                }
            }
            else -> super.onNavigationEvent(type, args)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    requestVerify(purchase)
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {

        } else {
            showToast(R.string.error_purchase)
        }
    }

    private fun requestSkuDetails(resultListener: ListenerResult<Boolean>?) {
        val purchasesInfo = billingClient?.queryPurchases(BillingClient.SkuType.INAPP)

        if (purchasesInfo?.purchasesList?.isNotEmpty() == true) {
            for (purchase in purchasesInfo.purchasesList) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    requestVerify(purchase)
                }
            }
        }
        if (listSkuDetails.isNotEmpty()) {
            resultListener?.onResult(true)
            return
        }

        val skuList = ArrayList<String>()
        val params = SkuDetailsParams.newBuilder()

        skuList.add(PRODUCT_1)
        skuList.add(PRODUCT_2)
        skuList.add(PRODUCT_3)
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                listSkuDetails = skuDetailsList
            }
            resultListener?.onResult(billingResult.responseCode == BillingClient.BillingResponseCode.OK)
        }
    }

    fun onChooseSub(subType: Int) {
        val productId = getProductId(subType) ?: return
        var buySkuDetails: SkuDetails? = null

        for (skuDetails in listSkuDetails) {
            if (skuDetails.sku == productId) {
                buySkuDetails = skuDetails
                break
            }
        }

        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(buySkuDetails)
            .build()
        billingClient?.launchBillingFlow(this, flowParams)
    }

    private fun initBilling(resultListener: ListenerResult<Boolean>? = null) {
        if (billingClient == null) {
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
        }
        if (isBillingServiceConnected) {
            requestSkuDetails(resultListener)
            return
        }

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isBillingServiceConnected = true
                    requestSkuDetails(resultListener)
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                    showToast("Billing unavailable")
                    resultListener?.onResult(false)
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
                    if (attemptConnect <= 3) {
                        showToast(R.string.loading)
                        attemptConnect++
                    } else {
                        resultListener?.onResult(false)
                    }
                } else {
                    resultListener?.onResult(false)
                }
            }
            override fun onBillingServiceDisconnected() {
                isBillingServiceConnected = false
                resultListener?.onResult(false)
            }
        })
    }

    private fun requestVerify(purchase: Purchase?) {
        if (purchase == null || !isNetworkAvailable()) return

        //TODO
        /*CoroutineScope(Dispatchers.Main).launch {
            val firebaseUser = RepositoryCommon.getInstance().firebaseUser
            val tokenId = RepositoryCommon.getInstance().tokenId

            setLoadingVisible(true)

            val purchaseResult = ApiHelper.requestVerifyPurchase(firebaseUser, tokenId, purchase.purchaseToken,
                purchase.signature, purchase.originalJson, purchase.sku)

            setLoadingVisible(false)
            if (purchaseResult?.result?.isSuccess() == true) {
                onPurchaseResultSuccess(purchaseResult)
            } else if (purchaseResult?.result?.code == RESULT_PURCHASE_ALREADY_RECEIVED) {
                consumePurchase(purchaseResult)
            }
        }*/
    }

    private fun consumePurchase(purchaseResult: PurchaseResult) {
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseResult.tokenId).build()

        billingClient?.consumeAsync(consumeParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                showToast(R.string.purchase_consumed)
            }
        }
    }

    private fun onPurchaseResultSuccess(purchaseResult: PurchaseResult) {
        consumePurchase(purchaseResult)
        //TODO
    }

    //------------------------------------------------------------------------------------------------------------------
    private fun getProductId(type: Int) = when(type) {
        0 -> PRODUCT_1
        1 -> PRODUCT_2
        2 -> PRODUCT_3
        else -> null
    }
}