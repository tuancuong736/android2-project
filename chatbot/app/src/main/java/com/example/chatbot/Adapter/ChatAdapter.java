package com.example.chatbot.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatbot.Model.Message;
import com.example.chatbot.R;

import java.util.ArrayList;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Activity activity;
    private int SELF = 100;
    private ArrayList<Message> messages;

    public ChatAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else {
            // WatBot message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_watson, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getId() != null && message.getId().equals("1")) {
            return SELF;
        }
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        switch (message.getType()) {
            case TEXT:
                ((ViewHolder) holder).message.setText(message.getMessage());
                break;
            case IMAGE:
                ((ViewHolder) holder).message.setVisibility(View.GONE);
                ImageView imageView = ((ViewHolder) holder).image;
                Glide
                        .with(imageView.getContext())
                        .load(message.getUrl())
                        .into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        ImageView image;

        public ViewHolder(View view) {
            super(view);
            message = itemView.findViewById(R.id.message);
            image = itemView.findViewById(R.id.image);
        }
    }
}
