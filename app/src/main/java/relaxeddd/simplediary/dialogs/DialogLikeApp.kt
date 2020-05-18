package relaxeddd.simplediary.dialogs

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.ListenerResult

class DialogLikeApp(private val confirmListener: ListenerResult<Boolean>?) : DialogFragment() {

    init {
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = MaterialAlertDialogBuilder(context!!)
        .setTitle(R.string.you_like_app)
        .setPositiveButton(R.string.yes) { _, _ -> confirmListener?.onResult(true) }
        .setNegativeButton(R.string.no) { _, _ -> confirmListener?.onResult(false) }
        .create()
}