package ir.vira.salam.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import ir.vira.network.NetworkInformation;
import ir.vira.salam.Enumerations.MessageType;
import ir.vira.salam.Models.MessageModel;
import ir.vira.salam.R;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private List<MessageModel> messageModels;
    private NetworkInformation networkInformation;

    public ChatRecyclerAdapter(List<MessageModel> messageModels, Context context) {
        this.messageModels = new ArrayList<>(messageModels);
        networkInformation = new NetworkInformation(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModels.get(position).getUserModel().getIp().equals(networkInformation.getIpAddress()))
            return MessageType.OWN.ordinal();
        else
            return MessageType.OTHER.ordinal();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MessageType.OWN.ordinal())
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.own_messages_item, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.others_messages_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.roundedImageView.setImageBitmap(messageModels.get(position).getUserModel().getProfile());
        holder.textViewText.setText(messageModels.get(position).getText());
        holder.textViewName.setText(messageModels.get(position).getUserModel().getName());
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public void newMsg(MessageModel messageModel) {
        messageModels.add(0, messageModel);
        notifyItemInserted(0);
    }
}

class ChatViewHolder extends RecyclerView.ViewHolder {

    TextView textViewName, textViewText;
    RoundedImageView roundedImageView;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        initializeViews();
    }

    private void initializeViews() {
        textViewName = itemView.findViewById(R.id.chatTextName);
        textViewText = itemView.findViewById(R.id.chatTextText);
        roundedImageView = itemView.findViewById(R.id.chatImageProfile);
    }
}
