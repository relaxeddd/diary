package relaxeddd.simplediary.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import org.koin.androidx.viewmodel.ext.android.viewModel
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.databinding.FragmentSettingsBinding
import relaxeddd.simplediary.dialogs.*
import relaxeddd.simplediary.ui.FragmentBase
import relaxeddd.simplediary.ui.main.ActivityMain
import java.lang.Exception

class FragmentSettings : FragmentBase<ViewModelSettings, FragmentSettingsBinding>() {

    private val listenerConfirmLogout: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            viewModel.onLogoutDialogResult(result)
        }
    }
    private val listenerSubscription: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            val activity = activity

            if (activity != null && activity is ActivityMain) {
                val args = Bundle()
                args.putInt(PRODUCT_TYPE, result)
                activity.onNavigationEvent(EventType.BUY_PRODUCT, args)
            }
        }
    }

    override fun getLayoutResId() = R.layout.fragment_settings
    override val viewModel: ViewModelSettings by viewModel()

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
    }

    @SuppressLint("BatteryLife")
    override fun onNavigationEvent(type: EventType, args: Bundle?) {
        when (type) {
            EventType.NAVIGATION_DIALOG_APP_ABOUT -> {
                if (isResumed) {
                    DialogAppAbout().show(this@FragmentSettings.childFragmentManager, "App Info Dialog")
                }
            }
            EventType.NAVIGATION_DIALOG_SUBSCRIPTION_INFO -> {
                if (isResumed) {
                    DialogSubscriptionInfo().show(this@FragmentSettings.childFragmentManager, "Sub Info Dialog")
                }
            }
            EventType.NAVIGATION_DIALOG_RECEIVE_HELP -> {
                val ctx = context ?: return

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val pkg = ctx.packageName
                    val pm = ContextCompat.getSystemService(ctx, PowerManager::class.java) ?: return

                    if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                        try {
                            startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:$pkg")))
                        } catch (e: Exception) {}
                    } else {
                        DialogInfoReceiveHelp().show(this@FragmentSettings.childFragmentManager, "Receive help Dialog")
                    }
                }
            }
            EventType.NAVIGATION_DIALOG_CONFIRM_LOGOUT -> {
                if (isResumed) {
                    val dialog = DialogConfirmLogout()
                    dialog.confirmListener = listenerConfirmLogout
                    dialog.show(this@FragmentSettings.childFragmentManager, "Confirm Logout Dialog")
                }
            }
            EventType.NAVIGATION_GOOGLE_LOGOUT -> {
                if (isResumed) {
                    activity?.let {
                        AuthUI.getInstance().signOut(it).addOnCompleteListener { resultTask ->
                            viewModel.onLogoutResult(resultTask.isSuccessful)
                        }
                    }
                }
            }
            EventType.NAVIGATION_WEB_PLAY_MARKET -> {
                activity?.openWebApplication()
            }
            EventType.NAVIGATION_DIALOG_THEME -> {
                if (isResumed) {
                    val dialog = DialogAppTheme()
                    dialog.show(this@FragmentSettings.childFragmentManager, "Theme Dialog")
                }
            }
            EventType.NAVIGATION_DIALOG_SUBSCRIPTION -> {
                val dialog = DialogSubscription()
                dialog.listener = listenerSubscription
                dialog.show(this@FragmentSettings.childFragmentManager, "Subscription Dialog")
            }
            else -> super.onNavigationEvent(type, args)
        }
    }
}