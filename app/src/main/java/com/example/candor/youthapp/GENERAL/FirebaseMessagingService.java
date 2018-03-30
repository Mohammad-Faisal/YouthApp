package com.example.candor.youthapp.GENERAL;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.candor.youthapp.R;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Mohammad Faisal on 2/12/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        //-    GETTING THE NOTIFICATION
        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();

        //-GETTING THE DATA
        String fromUserName = remoteMessage.getData().get("user_name");
        String fromUserID = remoteMessage.getData().get("user_id");


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this , getString(R.string.default_notification_channel_id))
                        .setSmallIcon(R.drawable.ic_bio_icon)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody);


        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("userID" , fromUserID);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);


        int mNotificationId =(int) System.currentTimeMillis();
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());



    }
}
