package relaxeddd.simplediary.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.ListenerResult
import relaxeddd.simplediary.common.SharedHelper

class DialogLikeApp : DialogFragment() {

    var confirmListener: ListenerResult<Boolean>? = null

    init {
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle(R.string.you_like_app)
                .setPositiveButton(R.string.yes) { _, _ ->
                    SharedHelper.setCancelledRateDialog(true)
                    confirmListener?.onResult(true)
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    SharedHelper.setCancelledRateDialog(true)
                    confirmListener?.onResult(false)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}