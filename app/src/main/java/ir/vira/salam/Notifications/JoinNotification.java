package ir.vira.salam.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.core.app.NotificationCompat;

import ir.vira.salam.Models.UserModel;
import ir.vira.salam.R;
import ir.vira.salam.Receivers.PendingReceiver.AcceptJoinReceiver;
import ir.vira.salam.Receivers.PendingReceiver.DeclineJoinReceiver;

public class JoinNotification {
    private static final String CHANNEL_NAME = "Join";
    private static final String CHANNEL_ID = "Salam";
    private static final int NOTIFY_ID = 21;
    private static final int NOTIFICATION_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;
    private NotificationChannel notificationChannel;
    private Notification notification;

    public void showNotification(UserModel userModel, Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NOTIFICATION_IMPORTANCE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intentDecline = new Intent(context, DeclineJoinReceiver.class);
        Intent intentAccept = new Intent(context, AcceptJoinReceiver.class);
        intentAccept.putExtra("user", userModel);
        intentDecline.putExtra("user", userModel);
        PendingIntent pendingIntentDecline = PendingIntent.getBroadcast(context, context.getResources().getInteger(R.integer.declineRequestCode), intentDecline, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(context, context.getResources().getInteger(R.integer.acceptRequestCode), intentAccept, PendingIntent.FLAG_UPDATE_CURRENT);
        SpannableString spannableStringAccept = new SpannableString("قبول");
        spannableStringAccept.setSpan(new ForegroundColorSpan(Color.parseColor("#4CAF50")), 0, spannableStringAccept.length(), 0);
        SpannableString spannableStringDecline = new SpannableString("لغو");
        spannableStringDecline.setSpan(new ForegroundColorSpan(Color.parseColor("#F44336")), 0, spannableStringDecline.length(), 0);
        notification = new NotificationCompat.Builder(context, CHANNEL_ID).setContentText(userModel.getName() + " در خواست عضویت کرده . آیا آنرا می پذیزید ؟ ").setContentTitle("در خواست عضویت").setSmallIcon(R.mipmap.salam_logo).addAction(new NotificationCompat.Action(R.drawable.ic_account, spannableStringAccept, pendingIntentAccept)).addAction(new NotificationCompat.Action(R.drawable.ic_account, spannableStringDecline, pendingIntentDecline)).setOnlyAlertOnce(true).setColor(Color.parseColor("#2A91AD")).setAutoCancel(true).build();
        notificationManager.notify(NOTIFY_ID, notification);

    }

    public static int getNotifyId() {
        return NOTIFY_ID;
    }
}
