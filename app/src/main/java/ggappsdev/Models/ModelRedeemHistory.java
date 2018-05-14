package ggappsdev.Models;

public class ModelRedeemHistory {

    private String redeemDate;
    private String skinName;
    private int skinPrice;
    private String skinImage;


    public ModelRedeemHistory() {

    }

    public ModelRedeemHistory(String redeemDate, String skinName, int skinPrice, String skinImage) {
        this.redeemDate = redeemDate;
        this.skinName = skinName;
        this.skinPrice = skinPrice;
        this.skinImage = skinImage;
    }

    public String getRedeemDate() {
        return redeemDate;
    }

    public void setRedeemDate(String redeemDate) {
        this.redeemDate = redeemDate;
    }

    public String getSkinName() {
        return skinName;
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }

    public int getSkinPrice() {
        return skinPrice;
    }

    public void setSkinPrice(int skinPrice) {
        this.skinPrice = skinPrice;
    }

    public String getSkinImage() {
        return skinImage;
    }

    public void setSkinImage(String skinImage) {
        this.skinImage = skinImage;
    }
}



