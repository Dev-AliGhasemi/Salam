package ir.vira.salam.Contracts;

import java.util.List;

import ir.vira.salam.Models.UserModel;

public interface UserContract extends Contract<UserModel> {
    UserModel findUserByIP(String ip);
}
