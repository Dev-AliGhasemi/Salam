package ir.vira.salam.Sockets;

import ir.vira.salam.Models.MessageModel;

public interface NewMsgEventListener extends EventListener {
    void newMsg(MessageModel messageModel);
}
