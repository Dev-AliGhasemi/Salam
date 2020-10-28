package ir.vira.salam.Repositories;

import java.util.ArrayList;
import java.util.List;

import ir.vira.salam.Contracts.MessageContract;
import ir.vira.salam.Models.MessageModel;

public class MessageRepository implements MessageContract {

    private List<MessageModel> messageModels;
    private static MessageRepository messageRepository;

    private MessageRepository() {
        messageModels = new ArrayList<>();
    }

    public static MessageRepository getInstance() {
        if (messageRepository == null)
            messageRepository = new MessageRepository();
        return messageRepository;
    }

    @Override
    public void addAll(List<MessageModel> items) {
        messageModels.addAll(items);
    }

    @Override
    public void add(MessageModel item) {

    }
}
