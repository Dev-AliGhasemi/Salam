package ir.vira.salam.Receivers.PendingReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import ir.vira.salam.Contracts.UserContract;
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory;
import ir.vira.salam.Enumerations.RepositoryType;
import ir.vira.salam.Models.UserModel;
import ir.vira.salam.Notifications.JoinNotification;
import ir.vira.salam.R;

public class DeclineJoinReceiver extends BroadcastReceiver {

    private UserModel userModel;

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        UserModel userModel = (UserModel) intent.getSerializableExtra("user");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(JoinNotification.getNotifyId());
        Thread thread = new Thread(() -> {
            try {
                UserContract userContract = (UserContract) RepositoryFactory.getRepository(RepositoryType.USER_REPO);
                userContract.removeUser(userContract.findUserByIP(userModel.getIp()));
                Socket socket = new Socket(userModel.getIp(), context.getResources().getInteger(R.integer.portNumber));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("event", "JOIN");
                jsonObject.put("requestStatus", "decline");
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
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
