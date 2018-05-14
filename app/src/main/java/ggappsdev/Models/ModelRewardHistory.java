package ggappsdev.Models;

public class ModelRewardHistory {

    private String awardname;
    private int award;
    private String date;

    public ModelRewardHistory(){}

    public ModelRewardHistory(String awardname, int award, String date) {
        this.awardname = awardname;
        this.award = award;
        this.date = date;
    }

    public String getAwardname() {
        return awardname;
    }

    public void setAwardname(String awardname) {
        this.awardname = awardname;
    }

    public int getAward() {
        return award;
    }

    public void setAward(int award) {
        this.award = award;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
