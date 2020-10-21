package ir.vira.salam.Threads;

import android.content.Context;

import java.io.IOException;
import java.net.Socket;

import ir.vira.salam.Sockets.ErrorSocketListener;
import ir.vira.salam.Sockets.SocketListener;

/**
 * This class for connect to server
 *
 * @author Ali Ghasemi
 */
public class ConnectToServerThread extends Thread {

    private String host;
    private Context context;
    private int port;
    private SocketListener socketListener;
    private ErrorSocketListener errorSocketListener;

    public void setupConnection(String host, int port, Context context, SocketListener socketListener, ErrorSocketListener errorSocketListener) {
        this.host = host;
        this.port = port;
        this.socketListener = socketListener;
        this.errorSocketListener = errorSocketListener;
        this.context = context;
        setPriority(MAX_PRIORITY);
    }

    @Override
    public void run() {
        super.run();
        try {
            Socket socket = new Socket(host, port);
            socketListener.connect(socket);
        } catch (IOException e) {
            e.printStackTrace();
            errorSocketListener.error(e.getMessage());
        }
    }
}
