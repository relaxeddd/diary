package relaxeddd.simplediary.ui.billing

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.ui.ActivityBase

abstract class ActivityBilling<VM : ViewModelBilling, B : ViewDataBinding> : ActivityBase<VM, B>() {

    override fun onNavigationEvent(type: EventType, args: Bundle?) {
        /*when (type) {
            EventType.BUY_PRODUCT -> {
                val productType = args?.getInt(PRODUCT_TYPE) ?: return
                viewModel.onChooseProduct(productType)
            }
            EventType.LAUNCH_BILLING_FLOW -> {
                if (isActivityResumed) {
                    val productId = args?.getString(PRODUCT_ID, null) ?: return

                    var buySkuDetails: SkuDetails? = null

                    for (skuDetails in ViewModelBilling.listSkuDetails) {
                        if (skuDetails.sku == productId) {
                            buySkuDetails = skuDetails
                            break
                        }
                    }

                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(buySkuDetails)
                        .build()
                    viewModel.billingClient?.launchBillingFlow(this, flowParams)
                }
            }
            else -> super.onNavigationEvent(type, args)
        }*/
    }




}