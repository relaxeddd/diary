package relaxeddd.simplediary.dialogs

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.ListenerResult

class DialogSendFeedback(private val confirmListener: ListenerResult<String>?) : DialogFragment() {

    private var editTextFeedback: TextInputEditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?) = MaterialAlertDialogBuilder(context)
        .setTitle(R.string.send_feedback)
        .setView(R.layout.dialog_send_feedback)
        .setPositiveButton(R.string.send_feedback) { _, _ ->
            run {
                confirmListener?.onResult(editTextFeedback?.text.toString())
            }
        }
        .create()

    override fun onStart() {
        super.onStart()
        editTextFeedback = dialog?.findViewById(R.id.edit_text_send_feedback)
    }
}