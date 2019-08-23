package relaxeddd.simplediary.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.ListenerResult

class DialogSendFeedback : DialogFragment() {

    private var editTextFeedback: TextInputEditText? = null
    private var confirmListener: ListenerResult<String>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle(R.string.send_feedback)
                .setView(R.layout.dialog_send_feedback)
                .setPositiveButton(R.string.send_feedback) { _, _ ->
                    run {
                        confirmListener?.onResult(editTextFeedback?.text.toString())
                    }
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        editTextFeedback = dialog?.findViewById(R.id.edit_text_send_feedback)
    }

    fun setConfirmListener(confirmListener: ListenerResult<String>?) {
        this.confirmListener = confirmListener
    }
}