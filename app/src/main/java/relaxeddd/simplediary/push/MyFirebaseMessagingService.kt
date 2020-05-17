package relaxeddd.simplediary.push

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.NOTIFICATIONS_CHANNEL_REMIND
import relaxeddd.simplediary.common.SharedPreferenceStorage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        var pushToken: String = ""

        fun initChannelNotifications(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = activity.getString(R.string.default_notification_channel_id)
                val notificationManager = activity.getSystemService(NotificationManager::class.java)
                val channel = NotificationChannel(channelId, NOTIFICATIONS_CHANNEL_REMIND, NotificationManager.IMPORTANCE_HIGH)

                channel.setSound(null, null)

                notificationManager?.createNotificationChannel(channel)
            }
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        pushToken = p0
        if (p0.isNotEmpty()) {
            SharedPreferenceStorage(applicationContext).setPushToken(p0)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
    }
}