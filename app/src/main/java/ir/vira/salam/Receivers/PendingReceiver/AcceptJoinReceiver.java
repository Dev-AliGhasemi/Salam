package ir.vira.salam.Receivers.PendingReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import ir.vira.salam.Contracts.MessageContract;
import ir.vira.salam.Contracts.UserContract;
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory;
import ir.vira.salam.Enumerations.RepositoryType;
import ir.vira.salam.Models.MessageModel;
import ir.vira.salam.Models.UserModel;
import ir.vira.salam.Notifications.JoinNotification;
import ir.vira.salam.R;
import ir.vira.salam.Repositories.MessageRepository;
import ir.vira.salam.Repositories.UserRepository;
import ir.vira.utils.EncryptionAlgorithm;
import ir.vira.utils.Utils;

public class AcceptJoinReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        UserModel userModel = (UserModel) intent.getSerializableExtra("user");
        SecretKey secretKey = new SecretKeySpec(intent.getByteArrayExtra("secretKey"), EncryptionAlgorithm.AES.name());
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(JoinNotification.getNotifyId());
        Thread thread = new Thread(() -> {
            try {
                Socket socket = new Socket(userModel.getIp(), context.getResources().getInteger(R.integer.portNumber));
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("event", "JOIN");
                jsonObject.put("requestStatus", "accept");
                UserContract userContract = (UserContract) RepositoryFactory.getRepository(RepositoryType.USER_REPO);
                JSONArray jsonArrayUsers = new JSONArray();
                for (UserModel user : userContract.getAll()) {
                    JSONObject jsonUser = new JSONObject();
                    jsonUser.put("name", Utils.encodeToString(Utils.encryptData(user.getName(), EncryptionAlgorithm.AES)));
                    jsonUser.put("ip", Utils.encodeToString(Utils.encryptData(user.getIp(), EncryptionAlgorithm.AES)));
                    if (user.getProfile() != null)
                        jsonUser.put("profile", Utils.encodeToString(Utils.encryptData(Utils.getEncodeImage(user.getProfile()), EncryptionAlgorithm.AES)));
                    else
                        jsonUser.put("profile", "");
                    jsonUser.put("secretKey", Utils.encodeToString(user.getSecretKey().getEncoded()));
                    jsonArrayUsers.put(jsonUser);
                }
                MessageContract messageContract = (MessageContract) RepositoryFactory.getRepository(RepositoryType.MESSAGE_REPO);
                JSONArray jsonArrayMessages = new JSONArray();
                for (MessageModel messageModel : messageContract.getAll()) {
                    JSONObject jsonMessage = new JSONObject();
                    jsonMessage.put("ip", Utils.encodeToString(Utils.encryptData(messageModel.getUserModel().getIp(), EncryptionAlgorithm.AES)));
                    jsonMessage.put("text", Utils.encodeToString(Utils.encryptData(messageModel.getText(), EncryptionAlgorithm.AES)));
                    jsonArrayMessages.put(jsonMessage);
                }
                jsonObject.put("users", jsonArrayUsers);
                jsonObject.put("messages", jsonArrayMessages);
                jsonObject.put("secretKey", Utils.encodeToString(Utils.generateKey(EncryptionAlgorithm.AES).getEncoded()));
                dataOutputStream.writeUTF(jsonObject.toString());
                dataOutputStream.flush();
                dataOutputStream.close();
                socket.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }
}
