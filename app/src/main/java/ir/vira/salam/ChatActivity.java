package ir.vira.salam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import ir.vira.salam.DesignPatterns.Factory.ThreadFactory;
import ir.vira.salam.Enumerations.EventType;
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
            JoinNotification joinNotification = new JoinNotification();
            joinNotification.showNotification(userModel, this);
        });
    }
}