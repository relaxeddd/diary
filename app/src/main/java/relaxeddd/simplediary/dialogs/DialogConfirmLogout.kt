package relaxeddd.simplediary.dialogs

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.ListenerResult

class DialogConfirmLogout(private val confirmListener: ListenerResult<Boolean>?) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?) = MaterialAlertDialogBuilder(requireContext())
        .setMessage(R.string.do_you_really_want_to_logout)
        .setPositiveButton(R.string.yes) { _, _ -> confirmListener?.onResult(true) }
        .setNegativeButton(R.string.no) { _, _ -> confirmListener?.onResult(false) }
        .create()
}
