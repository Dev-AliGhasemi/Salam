package ir.vira.salam.Threads;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Patterns;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import ir.vira.salam.Contracts.UserContract;
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory;
import ir.vira.salam.Enumerations.EventType;
import ir.vira.salam.Enumerations.RepositoryType;
import ir.vira.salam.Models.MessageModel;
import ir.vira.salam.Models.UserModel;
import ir.vira.salam.Sockets.EventListener;
import ir.vira.salam.Sockets.JoinEventListener;
import ir.vira.salam.Sockets.NewMsgEventListener;
import ir.vira.utils.EncryptionAlgorithm;
import ir.vira.utils.Utils;

public class ReceiveEventsThread extends Thread {

    private ServerSocket serverSocket;
    private HashMap<EventType, EventListener> listeners;

    public void setup(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        listeners = new HashMap<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                String data = dataInputStream.readUTF();
                JSONObject jsonObject = new JSONObject(data);
                switch (EventType.valueOf(jsonObject.getString("event"))) {
                    case JOIN:
                        SecretKey secretKey = new SecretKeySpec(Utils.decodeToByte(jsonObject.getString("secretKey")), EncryptionAlgorithm.AES.name());
                        String ip = Utils.decryptData(Utils.decodeToByte(jsonObject.getString("ip")), secretKey, EncryptionAlgorithm.AES);
                        String name = Utils.decryptData(Utils.decodeToByte(jsonObject.getString("name")), secretKey, EncryptionAlgorithm.AES);
                        Bitmap profile = Utils.getBitmap(Utils.decryptData(Utils.decodeToByte(jsonObject.getString("profile")), secretKey, EncryptionAlgorithm.AES));
                        UserModel userModel = new UserModel(ip, name, profile, secretKey);
                        socket.close();
                        ((JoinEventListener) listeners.get(EventType.JOIN)).join(userModel);
                        break;
                    case NEW_MSG:
                        UserContract userContract = (UserContract) RepositoryFactory.getRepository(RepositoryType.USER_REPO);
                        SecretKey key = userContract.findUserByIP(jsonObject.getString("ip")).getSecretKey();
                        ip = jsonObject.getString("ip");
                        String text = Utils.decryptData(Utils.decodeToByte(jsonObject.getString("text")), key, EncryptionAlgorithm.AES);
                        MessageModel messageModel = new MessageModel(userContract.findUserByIP(ip), text);
                        socket.close();
                        ((NewMsgEventListener) listeners.get(EventType.NEW_MSG)).newMsg(messageModel);
                        break;
                    default:
                        Exception exception = new Exception("event type invalid !");
                        exception.printStackTrace();
                        break;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                run();
            }
        }
    }

    public void on(EventType eventType, EventListener eventListener) {
        listeners.put(eventType, eventListener);
    }
}
