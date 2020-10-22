package ir.vira.salam.Models;

public class MessageModel {

    private UserModel userModel;
    private String text;

    public MessageModel(UserModel userModel, String text) {
        this.userModel = userModel;
        this.text = text;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public String getText() {
        return text;
    }
}
