package relaxeddd.simplediary.ui.settings

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import relaxeddd.simplediary.App
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.User
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.domain.model.NavigationEvent
import relaxeddd.simplediary.model.repository.RepositoryPreferences
import relaxeddd.simplediary.model.repository.RepositoryUsers
import relaxeddd.simplediary.viewmodel.ViewModelBase

class ViewModelSettings(app: App, private val repositoryUsers: RepositoryUsers, preferences: RepositoryPreferences)
    : ViewModelBase() {

    private val userObserver = Observer<User?> { user ->
        var subTime = user?.subscriptionTime ?: System.currentTimeMillis()
        subTime -= System.currentTimeMillis()
        if (subTime < 0) subTime = 0
        liveDataSubDays.value = (subTime / 1000 / 60 / 60 / 24).toString()
    }

    val user: LiveData<User?> = repositoryUsers.user
    val liveDataSubDays = MutableLiveData("")
    val textTheme: String = "" /*getApplication<App>().resources.getStringArray(R.array.array_themes)[preferences.appThemeType]*/
    val isVisibleReceiveHelp = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    val clickListenerAppInfo = View.OnClickListener {
        navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_APP_ABOUT)
    }
    val clickListenerSubscription = View.OnClickListener {
        //if (ViewModelBilling.isBillingInit) {
            //navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_SUBSCRIPTION)
        //} else {
            //showToast(R.string.loading)
        //}
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
        /*val args = Bundle()
        args.putInt(ITEM_IX, preferences.appThemeType)
        navigateEvent.value = NavigationEvent(EventType.NAVIGATION_DIALOG_THEME, args)*/
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

    fun onAppThemeDialogResult(themeIx: Int) {
        /*if (themeIx != preferences.appThemeType) {
            preferences.setAppThemeType(themeIx)
            navigateEvent.value = NavigationEvent(EventType.NAVIGATION_RECREATE_ACTIVITY)
        }*/
    }

    fun onLogoutResult(isSuccess: Boolean) {
        /*if (isSuccess) {
            ioScope.launch {
                repositoryUsers.deleteUserInfo()
            }
        } else {
            showToast(R.string.logout_error)
        }*/
    }
}