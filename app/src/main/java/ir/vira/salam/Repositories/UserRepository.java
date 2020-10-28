package ir.vira.salam.Repositories;

import java.util.ArrayList;
import java.util.List;

import ir.vira.salam.Contracts.UserContract;
import ir.vira.salam.Models.UserModel;

public class UserRepository implements UserContract {
    private List<UserModel> userModels;
    private static UserRepository userRepository;

    private UserRepository() {
        userModels = new ArrayList<>();
    }

    public static UserRepository getInstance() {
        if (userRepository == null)
            userRepository = new UserRepository();
        return userRepository;
    }

    @Override
    public void addAll(List<UserModel> items) {
        userModels.addAll(items);
    }

    @Override
    public void add(UserModel item) {

    }

    @Override
    public UserModel findUserByIP(String ip) {
        for (int i = 0; i < userModels.size(); i++) {
            if (userModels.get(i).getIp().equals(ip))
                return userModels.get(i);
        }
        return null;
    }
}
