package relaxeddd.simplediary.ui.billing

import android.os.Bundle
import com.android.billingclient.api.*
import relaxeddd.simplediary.App
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.common.theme.ThemedActivityDelegate
import relaxeddd.simplediary.model.repository.RepositoryPreferences
import relaxeddd.simplediary.ui.ViewModelBase

abstract class ViewModelBilling(app: App, preferences: RepositoryPreferences)
        : ViewModelBase(app, preferences), PurchasesUpdatedListener {

    companion object {
        @BillingClient.SkuType private const val PRODUCT_1 = "product_1"
        @BillingClient.SkuType private const val PRODUCT_2 = "product_2"
        @BillingClient.SkuType private const val PRODUCT_3 = "product_3"

        var listSkuDetails: List<SkuDetails> = ArrayList()
        var isBillingInit = false
            private set
    }

    var billingClient: BillingClient? = null
        private set
    private var isBillingServiceConnected = false
    private var attemptConnect = 0
    var isBillingInInitProcess = false

    abstract fun onShowLoadingAction()
    abstract fun onHideLoadingAction()

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

    private fun initBilling(resultListener: ListenerResult<Boolean>? = null) {
        if (isBillingInit || isBillingInInitProcess) return

        isBillingInInitProcess = true
        if (billingClient == null) {
            billingClient = BillingClient.newBuilder(getApplication()).enablePendingPurchases().setListener(this).build()
        }
        val innerResultListener = object: ListenerResult<Boolean> {
            override fun onResult(result: Boolean) {
                isBillingInInitProcess = false
                isBillingInit = result
                resultListener?.onResult(result)
            }
        }
        if (isBillingServiceConnected) {
            requestSkuDetails(innerResultListener)
            return
        }

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isBillingServiceConnected = true
                    requestSkuDetails(innerResultListener)
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                    showToast("Billing unavailable")
                    innerResultListener.onResult(false)
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
                    if (attemptConnect <= 3) {
                        showToast(R.string.loading)
                        attemptConnect++
                    } else {
                        innerResultListener.onResult(false)
                    }
                } else {
                    innerResultListener.onResult(false)
                }
            }
            override fun onBillingServiceDisconnected() {
                isBillingServiceConnected = false
                innerResultListener.onResult(false)
            }
        })
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

    fun onChooseProduct(productType: Int) {
        val productId = getProductId(productType) ?: return
        val args = Bundle()

        if (isBillingInit) {
            args.putString(PRODUCT_ID, productId)
            navigateEvent.value = NavigationEvent(EventType.LAUNCH_BILLING_FLOW, args)
        } else if (!isBillingInInitProcess) {
            val resultListener = object: ListenerResult<Boolean> {
                override fun onResult(result: Boolean) {
                    if (result) {
                        args.putString(PRODUCT_ID, productId)
                        navigateEvent.value = NavigationEvent(EventType.LAUNCH_BILLING_FLOW, args)
                    }
                }
            }
            initBilling(resultListener)
        }
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

    private fun consumePurchase(purchaseContent: PurchaseContent) {
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseContent.tokenId).build()

        billingClient?.consumeAsync(consumeParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                showToast(R.string.purchase_consumed)
            }
        }
    }

    private fun onPurchaseResultSuccess(purchaseContent: PurchaseContent) {
        consumePurchase(purchaseContent)
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