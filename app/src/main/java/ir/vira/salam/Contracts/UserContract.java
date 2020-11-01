package ir.vira.salam.Contracts;

import ir.vira.salam.Models.UserModel;

public interface UserContract extends Contract<UserModel> {
    UserModel findUserByIP(String ip);
}
