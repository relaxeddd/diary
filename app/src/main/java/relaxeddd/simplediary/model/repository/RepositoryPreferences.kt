package relaxeddd.simplediary.model.repository

import androidx.lifecycle.MutableLiveData
import relaxeddd.simplediary.BuildConfig
import relaxeddd.simplediary.common.SharedPreferenceStorage

class RepositoryPreferences(private val sharedPreferenceStorage: SharedPreferenceStorage) {

    val launchCount: Int get() = sharedPreferenceStorage.getLaunchCount()
    val isCancelledRateDialog: Boolean get() = sharedPreferenceStorage.isCancelledRateDialog()
    val isPatchNotesViewed: Boolean get() = sharedPreferenceStorage.isPatchNotesViewed(BuildConfig.VERSION_NAME)
    val pushToken: String get() = sharedPreferenceStorage.getPushToken()
    //val appThemeType: Int get() = sharedPreferenceStorage.getAppThemeType()
    val isPrivacyPolicyConfirmed: MutableLiveData<Boolean> = MutableLiveData(sharedPreferenceStorage.isPrivacyPolicyConfirmed())

    var selectedTheme: String? = sharedPreferenceStorage.selectedTheme
    val observableSelectedTheme = sharedPreferenceStorage.observableSelectedTheme

    fun setPrivacyPolicyConfirmed() {
        sharedPreferenceStorage.setPrivacyPolicyConfirmed(true)
        isPrivacyPolicyConfirmed.value = true
    }

    fun setPatchNotesViewed() {
        sharedPreferenceStorage.setPatchNotesViewed(BuildConfig.VERSION_NAME)
    }

    fun setAppThemeType(type: Int) {
        //sharedPreferenceStorage.setAppThemeType(type)
    }

    fun setCancelledRateDialog() {
        sharedPreferenceStorage.setCancelledRateDialog(true)
    }
}