package com.example.group6finalgroupproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.databinding.ActivityChatHistoryBinding;
import com.example.group6finalgroupproject.databinding.ActivityChatRoomBinding;
import com.example.group6finalgroupproject.databinding.ChatHistoryItemBinding;
import com.example.group6finalgroupproject.databinding.ChatRoomItemBinding;
import com.example.group6finalgroupproject.model.ChatRoom;
import com.example.group6finalgroupproject.model.MessageItem;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    private ChatRoom chatRoom;
    ActivityChatRoomBinding binding;

    public ChatRoomAdapter(ChatRoom room) {
//        super();
        this.chatRoom = room;
    }

    /**
     * Update which chatRoom this adapter shows
     *
     * @param chatRoom
     */
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @NonNull
    @Override
    public ChatRoomAdapter.ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ChatRoomItemBinding binding = ChatRoomItemBinding.inflate(layoutInflater, parent, false);
        return new ChatRoomAdapter.ChatRoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomAdapter.ChatRoomViewHolder holder, int position) {
        MessageItem messageItem = chatRoom.getChatList().get(position);
        holder.bind(messageItem);
    }

    @Override
    public int getItemCount() {
        return chatRoom.getChatList().size();
    }

    /**
     * ViewHolder for individual message bubbles
     */
    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        private final ChatRoomItemBinding binding;

        public ChatRoomViewHolder(ChatRoomItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Display the message text and choose background
         * based on whether it came from user or assistant.
         */
        public void bind(MessageItem messageItem) {
            Context context = binding.getRoot().getContext();
            String message = messageItem.getMessage();
            binding.messageItem.setText(message);

            if (messageItem.getFrom().equals(context.getString(R.string.user_string))) {
                binding.messageContainer.setBackgroundResource(R.drawable.bg_user_message_pill);
            } else {
                binding.messageContainer.setBackgroundResource(R.drawable.bg_assistant_message_pill);
            }
        }
    }
}
