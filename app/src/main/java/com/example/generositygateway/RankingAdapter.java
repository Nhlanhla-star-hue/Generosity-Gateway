package com.example.generositygateway;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<RankingEntry> rankingList;

    public RankingAdapter(List<RankingEntry> rankingList) {
        this.rankingList = rankingList;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking_header, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            RankingEntry entry = rankingList.get(position - 1); // Adjust position to account for the header
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.rankTextView.setText(entry.getRank());
            itemViewHolder.usernameTextView.setText(entry.getUsername());
            itemViewHolder.totalQuantityTextView.setText(entry.getTotalQuantityDonated());
        }
    }

    @Override
    public int getItemCount() {
        return rankingList.size() + 1; // Add 1 to account for the header row
    }

    public List<RankingEntry> getRankingList() {
        return rankingList;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView;
        TextView usernameTextView;
        TextView totalQuantityTextView;

        ItemViewHolder(View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rank_text_view);
            usernameTextView = itemView.findViewById(R.id.username_text_view);
            totalQuantityTextView = itemView.findViewById(R.id.total_quantity_text_view);
        }
    }
}
