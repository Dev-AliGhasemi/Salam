package ir.vira.salam.Models;

import android.graphics.Bitmap;

import javax.crypto.SecretKey;

public class UserModel {
    private String ip;
    private String name;
    private Bitmap profile;
    private SecretKey secretKey;

    public UserModel(String ip, String name, Bitmap profile, SecretKey secretKey) {
        this.ip = ip;
        this.name = name;
        this.profile = profile;
        this.secretKey = secretKey;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public Bitmap getProfile() {
        return profile;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}
