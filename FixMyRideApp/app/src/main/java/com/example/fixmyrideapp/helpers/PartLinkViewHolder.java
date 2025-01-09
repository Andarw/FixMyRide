package com.example.fixmyrideapp.helpers;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixmyrideapp.R;

public class PartLinkViewHolder extends RecyclerView.ViewHolder {
    TextView partLinkTextView;

    public PartLinkViewHolder(View itemView) {
        super(itemView);
        partLinkTextView = itemView.findViewById(R.id.partLinkTextView);
    }
}