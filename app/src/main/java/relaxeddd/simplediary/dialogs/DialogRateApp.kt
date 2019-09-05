package relaxeddd.simplediary.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.ListenerResult

class DialogRateApp : DialogFragment() {

    var confirmListener: ListenerResult<Boolean>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle(R.string.rate_app_question)
                .setPositiveButton(R.string.yes) { _, _ -> confirmListener?.onResult(true) }
                .setNegativeButton(R.string.no) { _, _ -> confirmListener?.onResult(false) }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}