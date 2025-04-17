package com.example.group6finalgroupproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group6finalgroupproject.databinding.ActivityChatHistoryBinding;
import com.example.group6finalgroupproject.databinding.ChatHistoryItemBinding;
import com.example.group6finalgroupproject.model.ChatRoom;

import java.util.List;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ChatHistoryViewHolder> {
    private List<ChatRoom> chatRooms;
    ActivityChatHistoryBinding binding;
    private OnItemClickListener listener;

    // Define an interface for click callbacks
    public interface OnItemClickListener {
        void onItemClick(ChatRoom chatRoom);
    }

    // Setter for the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ChatHistoryAdapter(List<ChatRoom> rooms) {
        this.chatRooms = rooms;
    }

    @NonNull
    @Override
    public ChatHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ChatHistoryItemBinding binding = ChatHistoryItemBinding.inflate(layoutInflater, parent, false);
        return new ChatHistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHistoryViewHolder holder, int position) {
        ChatRoom chatRoom = chatRooms.get(position);
        holder.bind(chatRoom, listener);
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public static class ChatHistoryViewHolder extends RecyclerView.ViewHolder {
        private final ChatHistoryItemBinding binding;

        public ChatHistoryViewHolder(ChatHistoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final ChatRoom chatRoom, final OnItemClickListener listener) {
            String title = chatRoom.getTitle();
            binding.chatTitle.setText(title);

            // Set an OnClickListener on the entire item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(chatRoom);
                    }
                }
            });
        }
    }
}
