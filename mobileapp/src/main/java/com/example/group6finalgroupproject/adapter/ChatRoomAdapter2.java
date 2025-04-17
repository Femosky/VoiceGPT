package com.example.group6finalgroupproject.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.databinding.ActivityMain2Binding;
import com.example.group6finalgroupproject.databinding.ChatRoomItem2Binding;
import com.example.group6finalgroupproject.model.ChatRoom2;
import com.example.group6finalgroupproject.model.MessageItem2;

public class ChatRoomAdapter2 extends RecyclerView.Adapter<ChatRoomAdapter2.ChatRoomViewHolder2> {
    private ChatRoom2 chatRoom;
    ActivityMain2Binding binding;

    public ChatRoomAdapter2(ChatRoom2 room) {
        this.chatRoom = room;
    }

    // Setters
    public void setChatRoom(ChatRoom2 chatRoom) {
        this.chatRoom = chatRoom;
    }

    @NonNull
    @Override
    public ChatRoomAdapter2.ChatRoomViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ChatRoomItem2Binding binding = ChatRoomItem2Binding.inflate(layoutInflater, parent, false);
        return new ChatRoomAdapter2.ChatRoomViewHolder2(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomAdapter2.ChatRoomViewHolder2 holder, int position) {
        MessageItem2 messageItem = chatRoom.getChatList().get(position);
        holder.bind(messageItem);
    }

    @Override
    public int getItemCount() {
        return chatRoom.getChatList().size();
    }

    public static class ChatRoomViewHolder2 extends RecyclerView.ViewHolder {
        private final ChatRoomItem2Binding binding;

        public ChatRoomViewHolder2(ChatRoomItem2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(MessageItem2 messageItem) {
            Context context = binding.getRoot().getContext();
            String message = messageItem.getMessage();
            binding.messageItem.setText(message);

            LinearLayout container = binding.container; // from the generated binding

            // CHAT POSITION BASED ON USER
            if (messageItem.getFrom().equals(context.getString(R.string.user_string))) {
                container.setGravity(Gravity.END);
                binding.chatBubble.setBackgroundResource(R.color.user_background);
            } else {
                container.setGravity(Gravity.START);
                binding.chatBubble.setBackgroundResource(R.color.assistant_background);
            }
        }

    }
}
