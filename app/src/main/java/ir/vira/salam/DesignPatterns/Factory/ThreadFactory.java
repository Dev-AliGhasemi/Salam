package ir.vira.salam.DesignPatterns.Factory;

import ir.vira.salam.Enumerations.ThreadType;
import ir.vira.salam.Threads.ConnectToServerThread;
import ir.vira.salam.Threads.ReceiveEventsThread;
import ir.vira.salam.Threads.SendEventsThread;

public class ThreadFactory {
    public static Thread getThread(ThreadType threadType) {
        switch (threadType) {
            case CONNECT_TO_SERVER:
                return new ConnectToServerThread();
            case RECEIVER_EVENT:
                return new ReceiveEventsThread();
            case SENDER_EVENT:
                return new SendEventsThread();
            default:
                Exception exception = new Exception("ThreadType invalid");
                exception.printStackTrace();
                return null;
        }
    }
}
