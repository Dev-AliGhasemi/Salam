package ir.vira.salam.Threads;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import ir.vira.salam.Enumerations.EventType;
import ir.vira.salam.Models.UserModel;
import ir.vira.salam.Sockets.EventListener;
import ir.vira.salam.Sockets.JoinEventListener;
import ir.vira.utils.EncryptionAlgorithm;
import ir.vira.utils.Utils;

public class ServerEventThread extends Thread {

    private int port;
    private ServerSocket serverSocket;
    private HashMap<EventType, EventListener> listeners;
    private Set<Socket> sockets;

    public void setup(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        listeners = new HashMap<>();
        sockets = new HashSet<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                byte[] buff = new byte[bufferedInputStream.available()];
                bufferedInputStream.read(buff);
                JSONObject jsonObject = new JSONObject(buff.toString());
                switch (EventType.valueOf(jsonObject.getString("event"))) {
                    case JOIN:
                        SecretKey secretKey = new SecretKeySpec(Utils.decodeToByte(jsonObject.getString("secretKey")), EncryptionAlgorithm.AES.name());
                        String ip = Utils.decryptData(Utils.decodeToByte(jsonObject.getString("ip")), secretKey, EncryptionAlgorithm.AES);
                        String name = Utils.decryptData(Utils.decodeToByte(jsonObject.getString("name")), secretKey, EncryptionAlgorithm.AES);
                        Bitmap profile = Utils.getBitmap(Utils.decryptData(Utils.decodeToByte(jsonObject.getString("profile")), secretKey, EncryptionAlgorithm.AES));
                        UserModel userModel = new UserModel(ip, name, profile, secretKey);
                        sockets.add(socket);
                        ((JoinEventListener) listeners.get(EventType.JOIN)).join(userModel);
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
