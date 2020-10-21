package ir.vira.salam.DesignPatterns.Factory;

import ir.vira.salam.Enumerations.ThreadType;
import ir.vira.salam.Threads.ConnectToServerThread;

public class ThreadFactory {
    public static Thread getThread(ThreadType threadType) {
        switch (threadType) {
            case CONNECT_TO_SERVER:
                return new ConnectToServerThread();
            default:
                Exception exception = new Exception("ThreadType invalid");
                exception.printStackTrace();
                return null;
        }
    }
}
