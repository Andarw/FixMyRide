package com.example.fixmyrideapp.helpers;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixmyrideapp.R;

import java.util.List;

public class PartLinkAdapter extends RecyclerView.Adapter<PartLinkViewHolder> {
    private List<String> partLinks;

    public PartLinkAdapter(List<String> partLinks) {
        this.partLinks = partLinks;
    }

    public void updateLinks(List<String> newLinks) {
        this.partLinks = newLinks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PartLinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_part_link, parent, false);
        return new PartLinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartLinkViewHolder holder, int position) {
        holder.partLinkTextView.setText(partLinks.get(position));
    }

    @Override
    public int getItemCount() {
        return partLinks.size();
    }
}
