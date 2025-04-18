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

    /**
     * Callback interface for item taps
     */
    public interface OnItemClickListener {
        void onItemClick(ChatRoom chatRoom);
    }

    /**
     * @param listener
     * Allow activity to register a click listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ChatHistoryAdapter(List<ChatRoom> rooms) {
        this.chatRooms = rooms;
    }

    public void setChatRooms(List<ChatRoom> newChatRooms) {
        this.chatRooms = newChatRooms;
    }

    @NonNull
    @Override
    public ChatHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ChatHistoryItemBinding binding = ChatHistoryItemBinding.inflate(layoutInflater, parent, false);
        return new ChatHistoryViewHolder(binding);
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ChatHistoryViewHolder holder, int position) {
        ChatRoom chatRoom = chatRooms.get(position);
        holder.bind(chatRoom, listener);
    }

    /**
     * @return
     */
    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    /**
     * ViewHolder for a single history entry
     */
    public static class ChatHistoryViewHolder extends RecyclerView.ViewHolder {
        private final ChatHistoryItemBinding binding;

        public ChatHistoryViewHolder(ChatHistoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Bind title text and click callback for each item.
         *
         * @param chatRoom
         * @param listener
         */
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
