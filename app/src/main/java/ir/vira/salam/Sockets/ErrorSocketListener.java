package ir.vira.salam.Sockets;

/**
 * When socket can not connect to server error method run and can use message of error
 *
 * @author Ali Ghasemi
 * @see SocketListener
 * @see ir.vira.salam.Threads.ConnectToServerThread
 */
@FunctionalInterface
public interface ErrorSocketListener {
    void error(String message);
}
