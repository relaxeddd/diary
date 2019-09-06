package relaxeddd.simplediary.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.launch
import relaxeddd.simplediary.App
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.repository.RepositoryCommon
import relaxeddd.simplediary.model.repository.RepositoryInit
import relaxeddd.simplediary.model.repository.RepositoryPreferences
import relaxeddd.simplediary.model.repository.RepositoryUsers
import relaxeddd.simplediary.ui.billing.ViewModelBilling

class ViewModelMain(app: App, preferences: RepositoryPreferences,
                    private val repositoryInit: RepositoryInit, private val repositoryUsers: RepositoryUsers,
                    private val repositoryCommon: RepositoryCommon) : ViewModelBilling(app, preferences) {

    val isShowLoading = MutableLiveData(false)
    val isShowHorizontalProgress = MutableLiveData(false)
    val isShowGoogleAuth = MutableLiveData(false)
    val isShowWarningSubscription = MutableLiveData(false)
    val isPrivacyPolicyTextVisible = preferences.isPrivacyPolicyConfirmed
    private var isRateDialogShown = false

    val clickListenerGoogleAuth = View.OnClickListener {
        if (isNetworkAvailable()) {
            navigateEvent.value = NavigationEvent(EventType.GOOGLE_AUTH)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private val userObserver = Observer<User?> { user ->
        val isAuthorized = user != null

        isShowGoogleAuth.value = !isAuthorized
        isShowWarningSubscription.value = isAuthorized && user?.subscriptionTime ?: 0 <= System.currentTimeMillis()

        if (isAuthorized) {
            val launchCount = preferences.launchCount

            if (!isRateDialogShown && !preferences.isCancelledRateDialog && launchCount % 3 == 0) {
                navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_LIKE_APP)
                isRateDialogShown = true
            }
            preferences.setPrivacyPolicyConfirmed()
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    init {
        repositoryUsers.user.observeForever(userObserver)
    }

    override fun onCleared() {
        super.onCleared()
        repositoryUsers.user.removeObserver(userObserver)
    }

    override fun onShowLoadingAction() {
        isShowLoading.value = true
    }

    override fun onHideLoadingAction() {
        isShowLoading.value = false
    }

    fun onViewCreate() {
        requestInit()

        if (!preferences.isPatchNotesViewed) {
            navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_PATCH_NOTES)
            preferences.setPatchNotesViewed()
        }
    }

    fun onFeedbackDialogResult(feedback: String) {
        uiScope.launch {
            navigateEvent.value = NavigationEvent(EventType.LOADING_SHOW)

            val result = repositoryCommon.sendFeedback(feedback)
            if (result.isSuccess) {
                showToast(R.string.thank_you)
            } else {
                showErrorIfIncorrect(result)
            }

            navigateEvent.value = NavigationEvent(EventType.LOADING_HIDE)
        }
    }

    fun onLikeAppDialogResult(isLikeApp: Boolean) {
        preferences.setCancelledRateDialog()
        if (isLikeApp) {
            navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_RATE_APP)
        } else {
            navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_SEND_FEEDBACK)
        }
    }

    fun requestInit() {
        isShowGoogleAuth.value = false
        isShowHorizontalProgress.value = true

        ioScope.launch {
            val initResult = repositoryInit.init()

            uiScope.launch {
                showErrorIfIncorrect(initResult) //TODO handle when push token is empty
                isShowHorizontalProgress.value = false
            }
        }
    }
}