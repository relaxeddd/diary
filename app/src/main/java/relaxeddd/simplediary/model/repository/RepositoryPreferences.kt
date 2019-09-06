package relaxeddd.simplediary.model.repository

import androidx.lifecycle.MutableLiveData
import relaxeddd.simplediary.BuildConfig
import relaxeddd.simplediary.common.SharedHelper

class RepositoryPreferences(private val sharedHelper: SharedHelper) {

    val launchCount: Int get() = sharedHelper.getLaunchCount()
    val isCancelledRateDialog: Boolean get() = sharedHelper.isCancelledRateDialog()
    val isPatchNotesViewed: Boolean get() = sharedHelper.isPatchNotesViewed(BuildConfig.VERSION_NAME)
    val pushToken: String get() = sharedHelper.getPushToken()
    val appThemeType: Int get() = sharedHelper.getAppThemeType()
    val isPrivacyPolicyConfirmed: MutableLiveData<Boolean> = MutableLiveData(sharedHelper.isPrivacyPolicyConfirmed())

    fun setPrivacyPolicyConfirmed() {
        sharedHelper.setPrivacyPolicyConfirmed(true)
        isPrivacyPolicyConfirmed.value = true
    }

    fun setPatchNotesViewed() {
        sharedHelper.setPatchNotesViewed(BuildConfig.VERSION_NAME)
    }

    fun setAppThemeType(type: Int) {
        sharedHelper.setAppThemeType(type)
    }

    fun setCancelledRateDialog() {
        sharedHelper.setCancelledRateDialog(true)
    }
}