package relaxeddd.simplediary.dialogs

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import relaxeddd.simplediary.BuildConfig
import relaxeddd.simplediary.R

class DialogAppAbout : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?) = MaterialAlertDialogBuilder(context!!)
        .setTitle(R.string.about_app)
        .setMessage(getString(R.string.text_app_about, BuildConfig.VERSION_NAME))
        .setPositiveButton(android.R.string.ok) { _, _ -> }
        .create()
}