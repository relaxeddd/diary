package relaxeddd.simplediary.ui.settings

import android.os.Build
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.launch
import relaxeddd.simplediary.App
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.repository.RepositoryUsers
import relaxeddd.simplediary.ui.ViewModelBase

class ViewModelSettings(app: App, private val repositoryUsers: RepositoryUsers, sharedHelper: SharedHelper) : ViewModelBase(app) {

    private val userObserver = Observer<User?> { user ->
        var subTime = user?.subscriptionTime ?: System.currentTimeMillis()
        subTime -= System.currentTimeMillis()
        if (subTime < 0) subTime = 0
        liveDataSubDays.value = (subTime / 1000 / 60 / 60 / 24).toString()
    }

    val user: LiveData<User?> = repositoryUsers.liveDataUser
    val liveDataSubDays = MutableLiveData("")
    val textTheme: String = App.context.resources.getStringArray(R.array.array_themes)[sharedHelper.getAppThemeType()]
    val isVisibleReceiveHelp = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    val clickListenerAppInfo = View.OnClickListener {
        navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_APP_ABOUT)
    }
    val clickListenerSubscription = View.OnClickListener {
        navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_SUBSCRIPTION)
    }
    val clickListenerSubscriptionInfo = View.OnClickListener {
        navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_SUBSCRIPTION_INFO)
    }
    val clickListenerSendFeedback = View.OnClickListener {
        navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_SEND_FEEDBACK)
    }
    val clickListenerLogout = View.OnClickListener {
        navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_CONFIRM_LOGOUT)
    }
    val clickListenerRate = View.OnClickListener {
        navigateEvent.value = NavigationEvent(EventType.NAVIGATION_WEB_PLAY_MARKET)
    }
    val clickListenerReceiveHelp = View.OnClickListener {
        navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_RECEIVE_HELP)
    }
    val clickListenerTheme = View.OnClickListener {
        navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_THEME)
    }

    init {
        user.observeForever(userObserver)
    }

    override fun onCleared() {
        super.onCleared()
        user.removeObserver(userObserver)
    }

    fun onLogoutDialogResult(isConfirmed: Boolean) {
        if (isConfirmed) {
            navigateEvent.value = NavigationEvent(EventType.NAVIGATION_GOOGLE_LOGOUT)
        }
    }

    fun onLogoutResult(isSuccess: Boolean) {
        if (isSuccess) {
            ioScope.launch {
                repositoryUsers.deleteUserInfo()
            }
        } else {
            showToast(R.string.logout_error)
        }
    }
}