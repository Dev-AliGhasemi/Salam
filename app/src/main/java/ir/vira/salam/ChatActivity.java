package ir.vira.salam;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.os.Bundle;

import java.io.IOException;

import ir.vira.salam.Contracts.UserContract;
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory;
import ir.vira.salam.DesignPatterns.Factory.ThreadFactory;
import ir.vira.salam.Enumerations.EventType;
import ir.vira.salam.Enumerations.RepositoryType;
import ir.vira.salam.Enumerations.ThreadType;
import ir.vira.salam.Models.UserModel;
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
        });
    }
}