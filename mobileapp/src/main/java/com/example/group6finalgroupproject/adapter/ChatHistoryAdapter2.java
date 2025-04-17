package com.example.group6finalgroupproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group6finalgroupproject.databinding.ActivityChatHistory2Binding;
import com.example.group6finalgroupproject.databinding.ChatHistoryItem2Binding;
import com.example.group6finalgroupproject.model.ChatRoom2;
import com.example.group6finalgroupproject.model.MessageItem2;
import com.example.group6finalgroupproject.utils.HelperUtils2;

import java.util.List;

public class ChatHistoryAdapter2 extends RecyclerView.Adapter<ChatHistoryAdapter2.ChatHistoryViewHolder2> {
    private List<ChatRoom2> chatRooms;
    ActivityChatHistory2Binding binding;
    private OnItemClickListener listener;

    // Define an interface for click callbacks
    public interface OnItemClickListener {
        void onItemClick(ChatRoom2 chatRoom);
    }

    // Setter for the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ChatHistoryAdapter2(List<ChatRoom2> rooms) {
        this.chatRooms = rooms;
    }

    @NonNull
    @Override
    public ChatHistoryViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ChatHistoryItem2Binding binding = ChatHistoryItem2Binding.inflate(layoutInflater, parent, false);
        return new ChatHistoryViewHolder2(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHistoryViewHolder2 holder, int position) {
        ChatRoom2 chatRoom = chatRooms.get(position);
        holder.bind(chatRoom, listener);
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public static class ChatHistoryViewHolder2 extends RecyclerView.ViewHolder {
        private final ChatHistoryItem2Binding binding;

        public ChatHistoryViewHolder2(ChatHistoryItem2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final ChatRoom2 chatRoom, final OnItemClickListener listener) {
            String title = chatRoom.getTitle();
            binding.chatTitle.setText(title);

            boolean isChatRoomEmpty = chatRoom.getChatList().isEmpty();
            if (!isChatRoomEmpty) {
                List<MessageItem2> messages = chatRoom.getChatList();
                int lastMessageIndex = chatRoom.getChatList().size() - 1;
                long latestTimestamp = messages.get(lastMessageIndex).getCreated();

                binding.dateTextView.setText(HelperUtils2.formatShortDate(latestTimestamp));
            }

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
