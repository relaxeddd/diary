package relaxeddd.simplediary.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.databinding.FragmentSettingsBinding
import relaxeddd.simplediary.dialogs.*
import relaxeddd.simplediary.ui.FragmentBase
import relaxeddd.simplediary.viewmodel.ViewModelSettings

class FragmentSettings : FragmentBase<ViewModelSettings, FragmentSettingsBinding>() {

    override fun getLayoutResId() = R.layout.fragment_settings
    override val viewModel = ViewModelSettings()

    override fun configureBinding() {
        super.configureBinding()
        //binding.viewModel = viewModel
    }

    @SuppressLint("BatteryLife")
    override fun onNavigationEvent(type: EventType, args: Bundle?) {
        when (type) {
            EventType.NAVIGATION_DIALOG_APP_ABOUT -> {
                showDialog(DialogAppAbout())
            }
            EventType.NAVIGATION_DIALOG_CONFIRM_LOGOUT -> {
                showDialog(DialogConfirmLogout(object: ListenerResult<Boolean> {
                    override fun onResult(result: Boolean) {
                        //viewModel.onLogoutDialogResult(result)
                    }
                }))
            }
            EventType.NAVIGATION_GOOGLE_LOGOUT -> {
                /*if (isResumed) {
                    AuthUI.getInstance().signOut(activity ?: return).addOnCompleteListener { resultTask ->
                        viewModel.onLogoutResult(resultTask.isSuccessful)
                    }
                }*/
            }
            EventType.NAVIGATION_DIALOG_THEME -> {
                val dialog = DialogAppTheme(object: ListenerResult<Int> {
                    override fun onResult(result: Int) {
                        //viewModel.onAppThemeDialogResult(result)
                    }
                })
                dialog.arguments = args
                showDialog(dialog)
            }
            EventType.NAVIGATION_RECREATE_ACTIVITY -> {
                activity?.recreate()
            }
            else -> super.onNavigationEvent(type, args)
        }
    }
}
