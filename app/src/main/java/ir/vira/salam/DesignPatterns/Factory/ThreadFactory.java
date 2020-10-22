package ir.vira.salam.DesignPatterns.Factory;

import ir.vira.salam.Enumerations.ThreadType;
import ir.vira.salam.Threads.ConnectToServerThread;
import ir.vira.salam.Threads.ServerEventThread;

public class ThreadFactory {
    public static Thread getThread(ThreadType threadType) {
        switch (threadType) {
            case CONNECT_TO_SERVER:
                return new ConnectToServerThread();
            case SERVER_EVENT:
                return new ServerEventThread();
            default:
                Exception exception = new Exception("ThreadType invalid");
                exception.printStackTrace();
                return null;
        }
    }
}
