package com.example.massa.luxvilla.notificacoes

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.example.massa.luxvilla.Actividades.casaactivity
import com.example.massa.luxvilla.R
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by massa on 27/10/2017.
 */
class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    internal var localcasa: String? = ""
    internal var precocasa: String? = ""
    internal var imgurlcasa: String? = ""
    internal var infocasa: String? = ""
    internal var idcasa: String? = ""

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]


        // TODO(developer): Handle FCM messages here.

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        Log.d(TAG, "From: " + remoteMessage!!.from)

        // Check if message contains a data payload.

        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        // Check if message contains a notification payload.

        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification.body!!)
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        localcasa = remoteMessage.data["localcasa"]
        precocasa = remoteMessage.data["precocasa"]
        imgurlcasa = remoteMessage.data["imgurl"]
        infocasa = remoteMessage.data["infocs"]
        idcasa = remoteMessage.data["csid"]

        sendNotification(remoteMessage.notification.body)
    }

    private fun sendNotification(messageBody: String?) {

        val intent = Intent(this, casaactivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("localcasa", localcasa)
        intent.putExtra("precocasa", precocasa)
        intent.putExtra("imgurl", imgurlcasa)
        intent.putExtra("infocs", infocasa)
        intent.putExtra("csid", idcasa)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Nova casa")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this@FirebaseMessagingService, R.color.colorPrimary))
                .setSound(defaultSoundUri)
                .setPriority(2)
                .setContentIntent(pendingIntent)

        val notificationManager =

                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {

        private val TAG = "FirebaseMsgService"
    }
}
