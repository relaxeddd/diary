package relaxeddd.simplediary.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import relaxeddd.simplediary.App
import relaxeddd.simplediary.BuildConfig
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.repository.RepositoryCommon
import relaxeddd.simplediary.model.repository.RepositoryUsers
import relaxeddd.simplediary.ui.billing.ViewModelBilling

class ViewModelMain(app: App, private val repositoryUsers: RepositoryUsers,
                    private val repositoryCommon: RepositoryCommon) : ViewModelBilling(app) {

    val isShowLoading = MutableLiveData(false)
    val isShowHorizontalProgress = MutableLiveData(false)
    val isShowGoogleAuth = MutableLiveData(false)
    val isShowWarningSubscription = MutableLiveData(false)
    private var isRateDialogShown = false

    private val userObserver = Observer<User?> { user ->
        isShowGoogleAuth.value = user == null || repositoryCommon.firebaseUser == null
        isShowWarningSubscription.value = user != null && user.subscriptionTime <= System.currentTimeMillis()

        val launchCount = SharedHelper.getLaunchCount()
        if (user != null && !isRateDialogShown && !SharedHelper.isCancelledRateDialog() && launchCount % 2 == 0) {
            isRateDialogShown = true
            navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_LIKE_APP)
        }
        if (user != null) {
            if (user.email.isNotEmpty()) {
                SharedHelper.setPrivacyPolicyConfirmed(true)
            }
        }
    }

    val clickListenerGoogleAuth = View.OnClickListener {
        if (!isNetworkAvailable()) return@OnClickListener
        navigateEvent.value = NavigationEvent(EventType.GOOGLE_AUTH)
    }

    init {
        repositoryUsers.liveDataUser.observeForever(userObserver)
    }

    override fun onCleared() {
        super.onCleared()
        repositoryUsers.liveDataUser.removeObserver(userObserver)
    }

    override fun onShowLoadingAction() {
        isShowLoading.value = true
    }

    override fun onHideLoadingAction() {
        isShowLoading.value = false
    }

    fun onViewCreate() {
        requestInit()

        if (!SharedHelper.isPatchNotesViewed(BuildConfig.VERSION_NAME)) {
            navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_PATCH_NOTES)
            SharedHelper.setPatchNotesViewed(BuildConfig.VERSION_NAME)
        }
    }

    fun onFeedbackDialogResult(feedback: String) {
        uiScope.launch {
            navigateEvent.value = NavigationEvent(EventType.LOADING_SHOW)
            repositoryCommon.sendFeedback(feedback)
            navigateEvent.value = NavigationEvent(EventType.LOADING_HIDE)
        }
    }

    fun requestInit() {
        if (repositoryUsers.isAuthorized()) {
            isShowGoogleAuth.value = false
            isShowHorizontalProgress.value = true
            ioScope.launch {
                val loginEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
                val savedEmail = SharedHelper.getUserEmail()

                if (savedEmail.isNotEmpty() && savedEmail != loginEmail) {
                    //RepositoryWord.getInstance().clearDictionary()
                    SharedHelper.setUserEmail(loginEmail)
                }
                repositoryUsers.init(object: ListenerResult<Boolean> {
                    override fun onResult(result: Boolean) {
                        if (!result) {
                            userObserver.onChanged(null)
                        }
                        isShowHorizontalProgress.value = false
                    }
                })
            }
        }
    }
}