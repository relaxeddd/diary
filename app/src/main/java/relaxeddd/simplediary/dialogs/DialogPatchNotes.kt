package relaxeddd.simplediary.dialogs

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import relaxeddd.simplediary.R

class DialogPatchNotes : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?) = MaterialAlertDialogBuilder(context!!)
        .setTitle(getString(R.string.new_version))
        .setMessage(getString(R.string.patch_notes))
        .setPositiveButton(android.R.string.ok) { _, _ -> }
        .create()
}