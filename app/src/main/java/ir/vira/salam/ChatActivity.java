package ir.vira.salam;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import java.io.IOException;
import ir.vira.network.NetworkInformation;
import ir.vira.salam.Contracts.MessageContract;
import ir.vira.salam.Contracts.UserContract;
import ir.vira.salam.DesignPatterns.Factory.ThreadFactory;
import ir.vira.salam.Enumerations.EventType;
import ir.vira.salam.Enumerations.ThreadType;
import ir.vira.salam.Models.MessageModel;
import ir.vira.salam.Models.UserModel;
import ir.vira.salam.Notifications.JoinNotification;
import ir.vira.salam.Repositories.MessageRepository;
import ir.vira.salam.Repositories.UserRepository;
import ir.vira.salam.Sockets.JoinEventListener;
import ir.vira.salam.Threads.ServerEventThread;
import ir.vira.utils.EncryptionAlgorithm;
import ir.vira.utils.Utils;

public class ChatActivity extends AppCompatActivity {

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (getIntent().getBooleanExtra("isAdmin", false)) {
            thread = ThreadFactory.getThread(ThreadType.SERVER_EVENT);
            try {
                Utils utils = Utils.getInstance(ChatActivity.this);
                UserContract userContract = UserRepository.getInstance();
                NetworkInformation networkInformation = new NetworkInformation(this);
                Bitmap profile = utils.getBitmap(R.drawable.ic_admin);
                userContract.add(new UserModel(networkInformation.getIpAddress(), "Admin", profile, Utils.generateKey(EncryptionAlgorithm.AES)));
                MessageContract messageContract = MessageRepository.getInstance();
                messageContract.add(new MessageModel(userContract.findUserByIP("0.0.0.0"),"fffffffffffffffffffffff"));
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