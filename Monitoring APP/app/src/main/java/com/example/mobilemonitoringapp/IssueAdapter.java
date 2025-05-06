package com.example.mobilemonitoringapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.ViewHolder> {

    private List<IssueItem> issueList;

    public IssueAdapter(List<IssueItem> issueList) {
        this.issueList = issueList;
    }

    @NonNull
    @Override
    public IssueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.issue_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueAdapter.ViewHolder holder, int position) {
        IssueItem issueItem = issueList.get(position);

        holder.issueName.setText(issueItem.getIssueName());

        
        holder.severity.setText("Loading explanations...");

        
        issueItem.getApplicationString(new ExplanationCallback() {
            @Override
            public void onComplete(String fullString) {
            
                if (holder.getAdapterPosition() == position) {
                    holder.itemView.post(() -> {
                        holder.severity.setText(fullString);
                    });
                }
            }

            @Override
            public void onExplanationReceived(String explanation) {
                
            }
        });
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView issueName, severity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            issueName = itemView.findViewById(R.id.issueName);
            severity = itemView.findViewById(R.id.applications);
        }
    }
}
