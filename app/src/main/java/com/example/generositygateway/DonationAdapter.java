package com.example.generositygateway;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DonationEntry> donationList;
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public DonationAdapter(List<DonationEntry> donationList) {
        this.donationList = donationList;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation_header, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
        } else {
            DonationEntry entry = donationList.get(position - 1);
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.itemTextView.setText(entry.getItem());
            itemViewHolder.quantityTextView.setText(String.valueOf(entry.getDonatedQuantity()));
            itemViewHolder.recipientTextView.setText(entry.getRecipientName());
        }
    }

    @Override
    public int getItemCount() {
        return donationList.size() + 1;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;
        TextView quantityTextView;
        TextView recipientTextView;

        ItemViewHolder(View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.item_text_view);
            quantityTextView = itemView.findViewById(R.id.quantity_text_view);
            recipientTextView = itemView.findViewById(R.id.recipient_text_view);
        }
    }
}
