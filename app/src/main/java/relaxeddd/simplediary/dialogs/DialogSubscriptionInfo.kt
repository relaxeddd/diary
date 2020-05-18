package relaxeddd.simplediary.dialogs

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import relaxeddd.simplediary.R

class DialogSubscriptionInfo : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?) = MaterialAlertDialogBuilder(context!!)
        .setTitle(R.string.sub_advantages)
        .setMessage(getString(R.string.sub_advantages_info))
        .setPositiveButton(android.R.string.ok) { _, _ -> }
        .create()
}