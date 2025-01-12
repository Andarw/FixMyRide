package com.example.fixmyrideapp.helpers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixmyrideapp.R;
import com.example.fixmyrideapp.ReportActivity;
import com.example.fixmyrideapp.entity.Report;

import java.util.List;

public class ReportsHistoryAdapter extends RecyclerView.Adapter<ReportsHistoryAdapter.ReportViewHolder> {

    private List<Report> reports;
    private Context context;

    public ReportsHistoryAdapter(List<Report> reports, Context context) {
        this.reports = reports;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reports.get(position);
        String title = report.getCarBrand() + " " + report.getCarModel() + " " + report.getCarYear();
        String date = report.getCreatedAt();
        String cost = report.getEstimatedCost();
        String damage = report.getDamagedLocation();
        holder.titleTextView.setText(title);
        holder.damageTextView.setText(damage);
        holder.costTextView.setText(cost);
        holder.dateTextView.setText(date);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReportActivity.class);
            intent.putExtra("reportId", report.getReportId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public void updateReports(List<Report> newReports) {
        reports.clear();
        reports.addAll(newReports);
        notifyDataSetChanged();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView costTextView;
        TextView damageTextView;
        TextView dateTextView;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.reportTitle);
            costTextView = itemView.findViewById(R.id.damageCost);
            damageTextView = itemView.findViewById(R.id.vehicleInfo);
            dateTextView = itemView.findViewById(R.id.reportDate);
        }
    }
}
