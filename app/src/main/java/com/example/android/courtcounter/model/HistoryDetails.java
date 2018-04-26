package com.example.android.courtcounter.model;

/**
 * Created by krishnachaitanya on 17/04/18.
 */

public class HistoryDetails {

    String sportSelected;
    String dateTime;
    int teamOneScore;
    String matchWinner;

    int teamTwoScore;
    String teamOneName;
    String teamTwoName;

    public HistoryDetails() {

    }

    public HistoryDetails(String sportSelected, String dateTime, int teamOneScore, int teamTwoScore, String teamOneName, String teamTwoName, String matchWinner) {
        this.sportSelected = sportSelected;
        this.dateTime = dateTime;
        this.teamOneName = teamOneName;
        this.teamTwoName = teamTwoName;
        this.teamOneScore = teamOneScore;
        this.teamTwoScore = teamTwoScore;
        this.matchWinner = matchWinner;
    }

    public String getSportSelected() {
        return sportSelected;
    }

    public void setSportsSelected(String sportSelected) {
        this.sportSelected = sportSelected;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTeamOneName() {
        return teamOneName;
    }

    public void setTeamOneName(String teamOneName) {
        this.teamOneName = teamOneName;
    }

    public String getTeamTwoName() {
        return teamTwoName;
    }

    public void setTeamTwoName(String teamTwoName) {
        this.teamTwoName = teamTwoName;
    }

    public int getTeamOneScore() {
        return teamOneScore;
    }

    public void setTeamOneScore(int teamOneScore) {
        this.teamOneScore = teamOneScore;
    }

    public int getTeamTwoScore() {
        return teamTwoScore;
    }

    public void setTeamTwoScore(int teamTwoScore) {
        this.teamTwoScore = teamTwoScore;
    }

    public String getMatchWinner(){
        return matchWinner;
    }
    public void setMatchWinner(String matchWinner){
        this.matchWinner = matchWinner;
    }

}
