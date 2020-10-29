package ir.vira.salam.Receivers.PendingReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import ir.vira.salam.Models.UserModel;
import ir.vira.salam.R;

public class DeclineJoinReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        UserModel userModel = (UserModel) intent.getSerializableExtra("user");
        Thread thread = new Thread(() -> {
            try {
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
