package ir.vira.salam.Sockets;

import java.net.Socket;

/**
 * When socket connect to server run connect method
 *
 * @author Ali Ghasemi
 * @see ir.vira.salam.Threads.ConnectToServerThread
 * @see ErrorSocketListener
 */
@FunctionalInterface
public interface SocketListener {
    void connect(Socket socket);
}
