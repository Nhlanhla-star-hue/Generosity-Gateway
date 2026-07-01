package com.example.generositygateway;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RecipientEntry> recipientList;

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public RecipientAdapter(List<RecipientEntry> recipientList) {
        this.recipientList = recipientList;
    }

    @Override
    public int getItemViewType(int position) {
        // The first position is always the header
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_requests_header, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_requests, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            RecipientEntry entry = recipientList.get(position - 1);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.item.setText(entry.getItem());
            itemHolder.initialQuantity.setText(String.valueOf(entry.getInitialQuantity()));
            itemHolder.receivedQuantity.setText(String.valueOf(entry.getReceivedQuantity()));
            itemHolder.donorUsername.setText(entry.getDonorUsername());
        }
    }

    @Override
    public int getItemCount() {
        return recipientList.size() + 1;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView item, initialQuantity, receivedQuantity, donorUsername;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.itemTextView);
            initialQuantity = itemView.findViewById(R.id.initialQuantityTextView);
            receivedQuantity = itemView.findViewById(R.id.receivedQuantityTextView);
            donorUsername = itemView.findViewById(R.id.donorUsernameTextView);
        }
    }
}
