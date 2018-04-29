package com.example.android.courtcounter.adaptors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.courtcounter.R;
import com.example.android.courtcounter.model.HistoryDetails;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by krishnachaitanya on 17/04/18.
 */

public class UsersHistoryListAdaptor extends RecyclerView.Adapter<UsersHistoryListAdaptor.ViewHolder> {


    public List<HistoryDetails> historyDetailsList;

    public UsersHistoryListAdaptor(List<HistoryDetails> historyDetailsList) {
        this.historyDetailsList = historyDetailsList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        HistoryDetails details = historyDetailsList.get(position);

        holder.matchDetails.setText(String.format("Game %s : ", position+1));
        holder.sportSelected.setText(details.getSportSelected());
        holder.dateTime.setText(details.getDateTime());
        holder.teamOneName.setText(details.getTeamOneName());
        holder.teamTwoName.setText(details.getTeamTwoName());
        holder.teamOneScore.setText(String.valueOf(details.getTeamOneScore()));
        holder.teamTwoScore.setText(String.valueOf(details.getTeamTwoScore()));
        holder.matchWinner.setText(details.getMatchWinner());
    }


    @Override
    public int getItemCount() {
        return historyDetailsList.size();
    }

    public void updateList(List<HistoryDetails> details) {
        historyDetailsList.clear();
        historyDetailsList.addAll(details);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public TextView sportSelected;
        public TextView dateTime;
        public TextView teamOneScore;
        public TextView teamTwoScore;
        public TextView teamOneName;
        public TextView teamTwoName;
        public TextView matchWinner;
        public TextView matchDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            matchDetails = mView.findViewById(R.id.match_details);
            sportSelected = mView.findViewById(R.id.history_sport_Selected);
            dateTime = mView.findViewById(R.id.history_dateTime);
            teamOneScore = mView.findViewById(R.id.history_teamOneScore);
            teamTwoScore = mView.findViewById(R.id.history_teamTwoScore);
            teamOneName = mView.findViewById(R.id.history_teamOneName);
            teamTwoName = mView.findViewById(R.id.history_teamTwoName);
            matchWinner = mView.findViewById(R.id.history_matchWinner);


        }
    }
}
