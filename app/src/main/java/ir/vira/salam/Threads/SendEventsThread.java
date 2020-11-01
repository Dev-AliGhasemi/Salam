package ir.vira.salam.Threads;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import ir.vira.salam.Enumerations.RepositoryType;
import ir.vira.salam.Models.MessageModel;
import ir.vira.salam.Models.UserModel;
import ir.vira.salam.R;
import ir.vira.utils.EncryptionAlgorithm;
import ir.vira.utils.Utils;

public class SendEventsThread extends Thread {

    private Context context;
    private MessageModel messageModel;

    public void setMessageModel(MessageModel messageModel, Context context) {
        this.messageModel = messageModel;
        this.context = context;
    }

    @Override
    public void run() {
        super.run();
        Socket socket;
        DataOutputStream dataOutputStream;
        JSONObject jsonObject = new JSONObject();
        UserContract userContract = (UserContract) RepositoryFactory.getRepository(RepositoryType.USER_REPO);
        NetworkInformation networkInformation = new NetworkInformation(context);
        try {
            jsonObject.put("event", "NEW_MSG");
            jsonObject.put("ip", messageModel.getUserModel().getIp());
            jsonObject.put("text", Utils.encodeToString(Utils.encryptData(messageModel.getText(), EncryptionAlgorithm.AES)));
            for (UserModel userModel : userContract.getAll()) {
                if (!userModel.getIp().equals(networkInformation.getIpAddress())) {
                    if (userModel.getIp().equals("0.0.0.0"))
                        socket = new Socket(networkInformation.getServerIpAddress(), context.getResources().getInteger(R.integer.portNumber));
                    else
                        socket = new Socket(userModel.getIp(), context.getResources().getInteger(R.integer.portNumber));
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeUTF(jsonObject.toString());
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    socket.close();
                }
            }
            MessageContract messageContract = (MessageContract) RepositoryFactory.getRepository(RepositoryType.MESSAGE_REPO);
            messageContract.add(messageModel);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
