package relaxeddd.simplediary.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.ListenerResult
import relaxeddd.simplediary.common.SharedHelper

class DialogAppTheme : DialogFragment() {

    private var selectedItemIx = 0
    var listener: ListenerResult<Int>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            selectedItemIx = SharedHelper.getAppThemeType(it)

            builder.setTitle(R.string.app_theme)
                .setSingleChoiceItems(R.array.array_themes, selectedItemIx) { _, which ->
                    selectedItemIx = which
                }.setPositiveButton(android.R.string.ok) { _, _ ->
                    if (selectedItemIx != SharedHelper.getAppThemeType(it)) {
                        SharedHelper.setAppThemeType(selectedItemIx, it)
                        activity?.recreate()
                        dismiss()
                    }

                }.setNegativeButton(android.R.string.cancel, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}