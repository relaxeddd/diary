package relaxeddd.simplediary.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.ITEM_IX
import relaxeddd.simplediary.common.ListenerResult

class DialogAppTheme(private val listener: ListenerResult<Int>?) : DialogFragment() {

    private var selectedItemIx = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        selectedItemIx = arguments?.getInt(ITEM_IX) ?: 0

        return MaterialAlertDialogBuilder(context!!)
            .setTitle(R.string.app_theme)
            .setSingleChoiceItems(R.array.array_themes, selectedItemIx) { _, which ->
                selectedItemIx = which
            }.setPositiveButton(android.R.string.ok) { _, _ ->
                listener?.onResult(selectedItemIx)
                dismiss()
            }.setNegativeButton(android.R.string.cancel, null)
            .create()
    }
}