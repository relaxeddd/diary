package relaxeddd.simplediary.dialogs

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import relaxeddd.simplediary.R

class DialogInfoReceiveHelp : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?) = MaterialAlertDialogBuilder(context!!)
        .setTitle(R.string.background_start)
        .setMessage(getString(R.string.text_dialog_receive_help))
        .setPositiveButton(android.R.string.ok) { _, _ -> }
        .create()
}