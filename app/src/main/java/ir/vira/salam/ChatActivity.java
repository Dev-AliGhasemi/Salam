package ir.vira.salam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ir.vira.network.NetworkInformation;
import ir.vira.salam.Contracts.MessageContract;
import ir.vira.salam.Contracts.UserContract;
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory;
import ir.vira.salam.DesignPatterns.Factory.ThreadFactory;
import ir.vira.salam.Enumerations.EventType;
import ir.vira.salam.Enumerations.RepositoryType;
import ir.vira.salam.Enumerations.ThreadType;
import ir.vira.salam.Models.MessageModel;
import ir.vira.salam.Models.UserModel;
import ir.vira.salam.Notifications.JoinNotification;
import ir.vira.salam.Repositories.MessageRepository;
import ir.vira.salam.Repositories.UserRepository;
import ir.vira.salam.Sockets.JoinEventListener;
import ir.vira.salam.Sockets.NewMsgEventListener;
import ir.vira.salam.Threads.ReceiveEventsThread;
import ir.vira.salam.Threads.SendEventsThread;
import ir.vira.utils.EncryptionAlgorithm;
import ir.vira.utils.Utils;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Thread receiverThread, senderThread;
    private EditText editTextMessage;
    private ImageView imageViewSend;
    private static TextView textViewMemberNum;
    private UserContract userContract;
    private static Activity activity;
    private JoinEventListener joinEventListener;

    public static TextView getTextViewMemberNum() {
        return textViewMemberNum;
    }

    public static Activity getActivity() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initializeViews();
        activity = this;
        try {
            receiverThread = ThreadFactory.getThread(ThreadType.RECEIVER_EVENT);
            userContract = (UserContract) RepositoryFactory.getRepository(RepositoryType.USER_REPO);
            ((ReceiveEventsThread) receiverThread).setup(getResources().getInteger(R.integer.portNumber));
            receiverThread.setPriority(Thread.MAX_PRIORITY);
            receiverThread.start();
            if (getIntent().getBooleanExtra("isAdmin", false)) {
                Utils utils = Utils.getInstance(ChatActivity.this);
                NetworkInformation networkInformation = new NetworkInformation(this);
                Bitmap profile = utils.getBitmap(R.drawable.ic_admin);
                userContract.add(new UserModel(networkInformation.getIpAddress(), "Admin", profile, Utils.generateKey(EncryptionAlgorithm.AES)));
                MessageContract messageContract = MessageRepository.getInstance();
                messageContract.add(new MessageModel(userContract.findUserByIP("0.0.0.0"), "fffffffffffffffffffffff"));
                initializeAdminEvents();
            } else {
                initializeClientEvents();
            }
            textViewMemberNum.setText(Utils.toPersian(" تعداد اعضا :" + userContract.getAll().size()));
            ((ReceiveEventsThread) receiverThread).on(EventType.JOIN, joinEventListener);
            ((ReceiveEventsThread) receiverThread).on(EventType.NEW_MSG, (NewMsgEventListener) messageModel -> {
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, messageModel.getText(), Toast.LENGTH_SHORT).show());
            });
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    private void initializeViews() {
        editTextMessage = findViewById(R.id.chatEditMessage);
        imageViewSend = findViewById(R.id.chatImageSend);
        textViewMemberNum = findViewById(R.id.chatTextMemberNum);
        imageViewSend.setOnClickListener(this::onClick);
    }

    private void initializeAdminEvents() {
        joinEventListener = userModel -> {
            UserContract userContract = (UserContract) RepositoryFactory.getRepository(RepositoryType.USER_REPO);
            userContract.add(userModel);
            JoinNotification joinNotification = new JoinNotification();
            joinNotification.showNotification(userModel, this);
        };
    }

    private void initializeClientEvents() {
        joinEventListener = userModel -> {
            UserContract userContract = (UserContract) RepositoryFactory.getRepository(RepositoryType.USER_REPO);
            userContract.add(userModel);
            textViewMemberNum.setText(Utils.toPersian(userContract.getAll().size() + "") + "تعداد اعضا :");
        };
    }

    @Override
    public void onClick(View v) {
        if (editTextMessage.getText().length() > 0) {
            senderThread = ThreadFactory.getThread(ThreadType.SENDER_EVENT);
            NetworkInformation networkInformation = new NetworkInformation(this);
            ((SendEventsThread) senderThread).setMessageModel(new MessageModel(userContract.findUserByIP(networkInformation.getIpAddress()), editTextMessage.getText().toString()), this);
            senderThread.setPriority(Thread.MAX_PRIORITY);
            senderThread.start();
        }
    }
}