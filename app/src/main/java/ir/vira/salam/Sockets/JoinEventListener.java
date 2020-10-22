package ir.vira.salam.Sockets;

import ir.vira.salam.Models.UserModel;

@FunctionalInterface
public interface JoinEventListener extends EventListener {
    void join(UserModel userModel);
}
