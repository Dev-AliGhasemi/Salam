package ir.vira.salam.DesignPatterns.Factory;

import java.io.IOError;
import java.io.IOException;

import ir.vira.salam.Contracts.Contract;
import ir.vira.salam.Enumerations.RepositoryType;
import ir.vira.salam.Repositories.MessageRepository;
import ir.vira.salam.Repositories.UserRepository;

public class RepositoryFactory {
    public static Contract getRepository(RepositoryType repositoryType) {
        switch (repositoryType) {
            case USER_REPO:
                return UserRepository.getInstance();
            case MESSAGE_REPO:
                return MessageRepository.getInstance();
            default:
                Exception exception = new Exception("repo type is invalid ! ");
                exception.printStackTrace();
                return null;
        }
    }
}
