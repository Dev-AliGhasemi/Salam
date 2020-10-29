package ir.vira.salam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import ir.vira.salam.Contracts.UserContract;
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory;
import ir.vira.salam.DesignPatterns.Factory.ThreadFactory;
import ir.vira.salam.Enumerations.EventType;
import ir.vira.salam.Enumerations.RepositoryType;
import ir.vira.salam.Enumerations.ThreadType;
import ir.vira.salam.Notifications.JoinNotification;
import ir.vira.salam.Sockets.JoinEventListener;
import ir.vira.salam.Threads.ServerEventThread;

public class ChatActivity extends AppCompatActivity {

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (getIntent().getBooleanExtra("isAdmin", false)) {
            thread = ThreadFactory.getThread(ThreadType.SERVER_EVENT);
            try {
                ((ServerEventThread) thread).setup(getResources().getInteger(R.integer.portNumber));
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.start();
                initializeServerEvents();
            } catch (IOException e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    private void initializeServerEvents() {
        ((ServerEventThread) thread).on(EventType.JOIN, (JoinEventListener) userModel -> {
            UserContract userContract = (UserContract) RepositoryFactory.getRepository(RepositoryType.USER_REPO);
            userContract.add(userModel);
            runOnUiThread(() -> {
                JoinNotification joinNotification = new JoinNotification();
                joinNotification.showNotification(userModel, this);
            });
            Log.e("join", "one use added !");
        });
    }
}