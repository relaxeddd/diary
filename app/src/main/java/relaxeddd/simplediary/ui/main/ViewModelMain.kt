package relaxeddd.simplediary.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import relaxeddd.simplediary.App
import relaxeddd.simplediary.BuildConfig
import relaxeddd.simplediary.common.EventType
import relaxeddd.simplediary.common.NavigationEvent
import relaxeddd.simplediary.common.SharedHelper
import relaxeddd.simplediary.common.isNetworkAvailable
import relaxeddd.simplediary.ui.ViewModelBase

class ViewModelMain(app: App) : ViewModelBase(app) {

    val isShowLoading = MutableLiveData(false)
    val isShowHorizontalProgress = MutableLiveData(false)
    val isShowGoogleAuth = MutableLiveData(false)

    val clickListenerGoogleAuth = View.OnClickListener {
        if (!isNetworkAvailable()) return@OnClickListener
        navigateEvent.value = NavigationEvent(EventType.GOOGLE_AUTH)
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
            //RepositoryCommon.getInstance().sendFeedback(feedback)
            navigateEvent.value = NavigationEvent(EventType.LOADING_HIDE)
        }
    }

    fun requestInit() {
        /*if (repositoryUser.isAuthorized()) {
            isShowGoogleAuth.value = false
            isShowHorizontalProgress.value = true
            ioScope.launch {
                val loginEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
                val savedEmail = SharedHelper.getUserEmail()

                if (savedEmail.isNotEmpty() && savedEmail != loginEmail) {
                    //RepositoryWord.getInstance().clearDictionary()
                    SharedHelper.setUserEmail(loginEmail)
                }
                repositoryUser.init(object: ListenerResult<Boolean> {
                    override fun onResult(result: Boolean) {
                        if (!result) {
                            userObserver.onChanged(null)
                        } else {
                            val user = user.value
                            if (user?.name?.isEmpty() == true) {
                                navigateEvent.value = Event(NAVIGATION_DIALOG_ENTER_NAME)
                            }
                        }
                        isShowHorizontalProgress.value = false
                    }
                })
            }
        }*/
    }
}