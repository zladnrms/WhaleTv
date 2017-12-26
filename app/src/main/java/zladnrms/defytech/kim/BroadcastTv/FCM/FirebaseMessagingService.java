package zladnrms.defytech.kim.BroadcastTv.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.orhanobut.logger.Logger;

import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.view.MainActivity;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Logger.t("FCM-RECEIVER").d(remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Logger.t("FCM-RECEIVER").d(remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Logger.t("FCM-RECEIVER").d(remoteMessage.getNotification().getBody());
        }

        //추가한것
        sendNotification(remoteMessage.getData().get("roomId") ,remoteMessage.getData().get("message"));
    }

    private void sendNotification(String roomId, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // roomId를 받아서 자신의 SQLite 내에서 같은 roomId를 가진 방제목을 가져와서 값을 다시 정해준다

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_record_start) // NOTI 옆에 아이콘
                .setContentTitle("방송이 시작됬어요!") // NOTI 제목
                .setContentText(messageBody) // NOTI 내용
                .setAutoCancel(true)
                .setSound(defaultSoundUri) // NOTI 소리
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
