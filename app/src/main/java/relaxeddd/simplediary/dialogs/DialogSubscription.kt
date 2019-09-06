package relaxeddd.simplediary.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.ListenerResult
import relaxeddd.simplediary.common.SELECTED_ITEM
import relaxeddd.simplediary.ui.billing.ViewModelBilling

class DialogSubscription(private val listener: ListenerResult<Int>?) : DialogFragment() {

    private var selectedItemIx: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        selectedItemIx = arguments?.getInt(SELECTED_ITEM, 0) ?: 0
        val subsNames = resources.getStringArray(R.array.array_renew_subscription)
        val subsNamesWithPrice = if (ViewModelBilling.listSkuDetails.size == subsNames.size) {
            Array(subsNames.size) { ix -> subsNames[ix] + " - " + ViewModelBilling.listSkuDetails[ix].price }
        } else {
            subsNames
        }

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.renew_subscription)
            .setSingleChoiceItems(subsNamesWithPrice, selectedItemIx) { _, which ->
                selectedItemIx = which
            }.setPositiveButton(android.R.string.ok) { _, _ ->
                listener?.onResult(selectedItemIx)
            }.setNegativeButton(android.R.string.cancel, null)
            .create()
    }
}