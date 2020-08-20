package relaxeddd.simplediary.ui.main

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.databinding.ActivityMainBinding
import relaxeddd.simplediary.databinding.NavigationHeaderBinding
import relaxeddd.simplediary.dialogs.DialogPatchNotes
import relaxeddd.simplediary.dialogs.DialogRateApp
import relaxeddd.simplediary.push.MyFirebaseMessagingService
import relaxeddd.simplediary.ui.NavigationHost
import relaxeddd.simplediary.ui.ActivityBase

import kotlin.system.exitProcess

class ActivityMain : ActivityBase<ActivityMainBinding>(), NavigationHost {

    companion object {
        const val EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID"

        const val REQUEST_SIGN_IN = 1312
        const val REQUEST_PLAY_SERVICES_RESULT = 7245

        private const val NAV_ID_NONE = -1

        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.navigation_todo_list,
            R.id.navigation_settings
        )
    }

    private lateinit var navigationController: NavController
    private lateinit var navigationHeaderBinding: NavigationHeaderBinding
    private var currentNavId = NAV_ID_NONE

    override fun getLayoutResId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyFirebaseMessagingService.initChannelNotifications(this)
        initGooglePlayServices()

        navigationHeaderBinding = NavigationHeaderBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@ActivityMain
        }

        navigationController = Navigation.findNavController(this, R.id.fragment_navigation_host)
        navigationController.addOnDestinationChangedListener { _, destination, _ ->
            currentNavId = destination.id
            val isTopLevelDestination = TOP_LEVEL_DESTINATIONS.contains(destination.id)
            val lockMode = if (isTopLevelDestination) {
                DrawerLayout.LOCK_MODE_UNLOCKED
            } else {
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            }

            binding.drawerLayoutMain.setDrawerLockMode(lockMode)
        }

        binding.navigationViewMain.apply {
            /*val menuView = findViewById<RecyclerView>(R.id.design_navigation_view)

            navigationHeaderBinding.root.doOnApplyWindowInsets { v, insets, padding ->
                v.updatePadding(top = padding.top + insets.systemWindowInsetTop)
                // NavigationView doesn't dispatch insets to the menu view, so pad the bottom here.
                menuView?.updatePadding(bottom = insets.systemWindowInsetBottom)
            }*/
            addHeaderView(navigationHeaderBinding.root)

            itemBackground = navigationItemBackground(context)

            setupWithNavController(this, navigationController)
        }

        if (savedInstanceState == null) {
            val initialNavId = intent.getIntExtra(EXTRA_NAVIGATION_ID, R.id.navigation_todo_list)
            binding.navigationViewMain.setCheckedItem(initialNavId)
            navigateTo(initialNavId)
        }


        /*navigation_view_main.setOnNavigationItemSelectedListener {
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
        }*/

        initPrivacyPolicyText()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        currentNavId = binding.navigationViewMain.checkedItem?.itemId ?: NAV_ID_NONE
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration.Builder(TOP_LEVEL_DESTINATIONS).setDrawerLayout(binding.drawerLayoutMain).build()
        setupWithNavController(toolbar, navigationController, appBarConfiguration)
    }

    override fun onNavigationEvent(type: EventType, args: Bundle?) {
        when (type) {
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
            EventType.NAVIGATION_DIALOG_PATCH_NOTES -> {
                showDialog(DialogPatchNotes())
            }
            else -> super.onNavigationEvent(type, args)
        }
    }

    override fun setupThemeColors() {
        super.setupThemeColors()
        /*navigation_view_main.setBackgroundResource(viewModel.primaryColorResId)
        navigation_view_main.itemBackgroundResource = viewModel.primaryColorResId*/
    }

    private fun initGooglePlayServices() {
        /*val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                val dialog = googleApiAvailability.getErrorDialog(this, status, REQUEST_PLAY_SERVICES_RESULT)
                dialog.setOnCancelListener { finish() }
                dialog.show()
            }
        } else {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        }*/
    }

    private fun initPrivacyPolicyText() {
        /*val privacyPolicy = text_main_privacy_policy.text.toString()
        val spannablePrivacyPolicy = SpannableString(privacyPolicy)
        val clickablePrivacyPolicy = object : ClickableSpan() {
            override fun onClick(textView: View) {
                openWebPrivacyPolicy()
            }
        }

        setClickableSubstring(privacyPolicy, spannablePrivacyPolicy, getString(R.string.privacy_policy_in_sentence), clickablePrivacyPolicy)

        text_main_privacy_policy.text = spannablePrivacyPolicy
        text_main_privacy_policy.movementMethod = LinkMovementMethod.getInstance()*/
    }

    private fun setClickableSubstring(string: String, spannableString: SpannableString, substring: String, clickableSpan: ClickableSpan) {
        val firstIndex = string.indexOf(substring)
        val lastIndex = firstIndex + substring.length

        spannableString.setSpan(clickableSpan, firstIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(UnderlineSpan(), firstIndex, lastIndex, 0)
    }

    private fun navigateTo(navId: Int) {
        if (navId != currentNavId) {
            navigationController.navigate(navId)
        }
    }
}
