package relaxeddd.simplediary.model.repository

import android.content.Context
import relaxeddd.simplediary.BuildConfig
import relaxeddd.simplediary.common.SharedHelper

class RepositoryPreferences(private val context: Context, private val sharedHelper: SharedHelper) {

    val launchCount: Int get() = sharedHelper.getLaunchCount(context)
    val isCancelledRateDialog: Boolean get() = sharedHelper.isCancelledRateDialog(context)
    val isPatchNotesViewed: Boolean get() = sharedHelper.isPatchNotesViewed(BuildConfig.VERSION_NAME, context)
    val pushToken: String get() = sharedHelper.getPushToken(context)
    val appThemeType: Int get() = sharedHelper.getAppThemeType(context)

    fun setPrivacyPolicyConfirmed() {
        sharedHelper.setPrivacyPolicyConfirmed(true, context)
    }

    fun setPatchNotesViewed() {
        sharedHelper.setPatchNotesViewed(BuildConfig.VERSION_NAME, context)
    }
}