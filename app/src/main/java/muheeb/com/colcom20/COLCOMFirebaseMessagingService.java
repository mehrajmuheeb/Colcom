package muheeb.com.colcom20;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class COLCOMFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = COLCOMFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    public static int counter;



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
        }

    }

    private void handleNotification(String message,String title) {
        Intent intent;
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
                if (title.equals("Feedback") && message.equals("Dear Students, Feedback forms are available")) {
                    intent = new Intent(this, Feedback.class);
                } else if (title.equals("Feedback") && message.equals("Respected Teacher, Check your feedback result"))
                {
                    intent = new Intent(this, FeedbackTeacher.class);
                }
                else if(title.trim().equals("You have a new message from"))
                {
//                    SharedPreferences.Editor editor = OTPConfirm.sharedPref2.edit();
//                    editor.putString("reciever", message);
//                    editor.commit();
                    intent = new Intent(this, MessageActivity.class);
                    intent.putExtra("reciever", message);
                }
                else{
                    counter=counter+1;
                    intent = new Intent(this, Notification_Activity.class);
                }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setContentText(message);
            notificationBuilder.setSmallIcon(R.drawable.logo);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            pushNotification.putExtra("title", title);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);



        }else{

            // If the app is in background, firebase itself handles the notification
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

        }
    }








}
