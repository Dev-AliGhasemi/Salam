package ir.vira.salam.Receivers.PendingReceiver;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import ir.vira.network.NetworkInformation;
import ir.vira.salam.ChatActivity;
import ir.vira.salam.Contracts.MessageContract;
import ir.vira.salam.Contracts.UserContract;
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory;
import ir.vira.salam.Enumerations.RepositoryType;
import ir.vira.salam.Models.MessageModel;
import ir.vira.salam.Models.UserModel;
import ir.vira.salam.Notifications.JoinNotification;
import ir.vira.salam.R;
import ir.vira.utils.EncryptionAlgorithm;
import ir.vira.utils.Utils;

public class AcceptJoinReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(JoinNotification.getNotifyId());
        Thread thread = new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("event", "JOIN");
                jsonObject.put("requestStatus", "accept");
                UserContract userContract = (UserContract) RepositoryFactory.getRepository(RepositoryType.USER_REPO);
                JSONArray jsonArrayUsers = new JSONArray();
                for (UserModel user : userContract.getAll()) {
                    JSONObject jsonUser = new JSONObject();
                    jsonUser.put("name", Utils.encodeToString(Utils.encryptData(user.getName(), EncryptionAlgorithm.AES)));
                    jsonUser.put("ip", Utils.encodeToString(Utils.encryptData(user.getIp(), EncryptionAlgorithm.AES)));
                    jsonUser.put("secretKey", Utils.encodeToString(user.getSecretKey().getEncoded()));
                    jsonUser.put("profile", Utils.getEncodeImage(user.getProfile()));
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
                Socket socket;
                jsonObject.put("users", jsonArrayUsers);
                jsonObject.put("messages", jsonArrayMessages);
                jsonObject.put("secretKey", Utils.encodeToString(Utils.generateKey(EncryptionAlgorithm.AES).getEncoded()));
                for (UserModel user : userContract.getAll()) {
                    if (!user.getIp().equals("0.0.0.0")) {
                        socket = new Socket(user.getIp(), context.getResources().getInteger(R.integer.portNumber));
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        dataOutputStream.writeUTF(jsonObject.toString());
                        dataOutputStream.flush();
                        dataOutputStream.close();
                        socket.close();
                    }
                }
                ChatActivity.getActivity().runOnUiThread(() -> ChatActivity.getTextViewMemberNum().setText(" تعداد اعضا :" + Utils.toPersian(userContract.getAll().size() + "")));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }
}
