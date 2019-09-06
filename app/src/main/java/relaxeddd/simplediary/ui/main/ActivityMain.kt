package relaxeddd.simplediary.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.databinding.ActivityMainBinding
import relaxeddd.simplediary.dialogs.DialogLikeApp
import relaxeddd.simplediary.dialogs.DialogPatchNotes
import relaxeddd.simplediary.dialogs.DialogRateApp
import relaxeddd.simplediary.dialogs.DialogSendFeedback
import relaxeddd.simplediary.push.MyFirebaseMessagingService
import relaxeddd.simplediary.ui.ActivityBase
import kotlin.system.exitProcess

class ActivityMain : ActivityBase<ViewModelMain, ActivityMainBinding>() {

    companion object {
        const val REQUEST_SIGN_IN = 1312
        const val REQUEST_PLAY_SERVICES_RESULT = 7245
    }

    private var selectedBottomMenuId: Int = R.id.fragmentTodoList
    private lateinit var navController: NavController
    override val viewModel: ViewModelMain by viewModel()
    private val providers: List<AuthUI.IdpConfig> = listOf(AuthUI.IdpConfig.GoogleBuilder().build())

    override fun getLayoutResId() = R.layout.activity_main

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyFirebaseMessagingService.initChannelNotifications(this)
        initGooglePlayServices()

        navController = Navigation.findNavController(this, R.id.fragment_navigation_host)
        navigation_view_main.setOnNavigationItemSelectedListener {
            if (it.itemId == selectedBottomMenuId) {
                return@setOnNavigationItemSelectedListener true
            }

            when (it.itemId) {
                R.id.fragmentTodoList -> navController.navigate(R.id.action_global_fragmentTodoList)
                R.id.fragmentSettings -> navController.navigate(R.id.action_global_fragmentSettings)
                else -> return@setOnNavigationItemSelectedListener false
            }
            selectedBottomMenuId = it.itemId

            return@setOnNavigationItemSelectedListener true
        }

        initPrivacyPolicyText()

        viewModel.onViewCreate()
    }

    override fun onBackPressed() {
        if (viewModel.isShowLoading.value == true) {
            viewModel.isShowLoading.value = false
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_SIGN_IN -> {
                val response: IdpResponse? = IdpResponse.fromResultIntent(data)

                if (resultCode == Activity.RESULT_OK) {
                    viewModel.requestInit()
                } else if (isActivityResumed && response != null) {
                    AuthUI.getInstance().signOut(this).addOnCompleteListener {}
                    Toast.makeText(this, response.error.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_PLAY_SERVICES_RESULT -> {
                finish()
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onNavigationEvent(type: EventType, args: Bundle?) {
        when (type) {
            EventType.LOADING_SHOW -> viewModel.onShowLoadingAction()
            EventType.LOADING_HIDE -> viewModel.onHideLoadingAction()
            EventType.NAVIGATION_FRAGMENT_TODO_LIST -> {
                if (navController.currentDestination?.label != getString(R.string.label_fragment_todo_list)) {
                    navController.navigate(R.id.action_global_fragmentTodoList)
                }
            }
            EventType.NAVIGATION_FRAGMENT_SETTINGS -> {
                if (navController.currentDestination?.label != getString(R.string.label_fragment_settings)) {
                    navController.navigate(R.id.action_global_fragmentSettings)
                }
            }
            EventType.NAVIGATION_DIALOG_RATE_APP -> {
                showDialog(DialogRateApp(object: ListenerResult<Boolean> {
                    override fun onResult(result: Boolean) {
                        if (result) {
                            openWebApplication()
                        }
                    }
                }))
            }
            EventType.EXIT -> {
                finishAffinity()
                exitProcess(0)
            }
            EventType.GOOGLE_AUTH -> {
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                    REQUEST_SIGN_IN
                )
            }
            EventType.NAVIGATION_DIALOG_PATCH_NOTES -> {
                showDialog(DialogPatchNotes())
            }
            EventType.GOOGLE_LOGOUT -> {
                if (isActivityResumed) {
                    viewModel.isShowLoading.value = true
                    AuthUI.getInstance().signOut(this).addOnCompleteListener {
                        viewModel.isShowLoading.value = false
                    }
                }
            }
            EventType.NAVIGATION_DIALOG_LIKE_APP -> {
                showDialog(DialogLikeApp(object: ListenerResult<Boolean> {
                    override fun onResult(result: Boolean) {
                        viewModel.onLikeAppDialogResult(result)
                    }
                }))
            }
            EventType.NAVIGATION_DIALOG_SEND_FEEDBACK -> {
                showDialog(DialogSendFeedback(object: ListenerResult<String> {
                    override fun onResult(result: String) {
                        viewModel.onFeedbackDialogResult(result)
                    }
                }))
            }
            else -> super.onNavigationEvent(type, args)
        }
    }

    override fun setupThemeColors() {
        super.setupThemeColors()
        navigation_view_main.setBackgroundResource(viewModel.primaryColorResId)
        navigation_view_main.itemBackgroundResource = viewModel.primaryColorResId
    }

    private fun initGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                val dialog = googleApiAvailability.getErrorDialog(this, status, REQUEST_PLAY_SERVICES_RESULT)
                dialog.setOnCancelListener { finish() }
                dialog.show()
            }
        } else {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        }
    }

    private fun initPrivacyPolicyText() {
        val privacyPolicy = text_main_privacy_policy.text.toString()
        val spannablePrivacyPolicy = SpannableString(privacyPolicy)
        val clickablePrivacyPolicy = object : ClickableSpan() {
            override fun onClick(textView: View) {
                openWebPrivacyPolicy()
            }
        }

        setClickableSubstring(privacyPolicy, spannablePrivacyPolicy, getString(R.string.privacy_policy_in_sentence), clickablePrivacyPolicy)

        text_main_privacy_policy.text = spannablePrivacyPolicy
        text_main_privacy_policy.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setClickableSubstring(string: String, spannableString: SpannableString, substring: String, clickableSpan: ClickableSpan) {
        val firstIndex = string.indexOf(substring)
        val lastIndex = firstIndex + substring.length

        spannableString.setSpan(clickableSpan, firstIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(UnderlineSpan(), firstIndex, lastIndex, 0)
    }
}