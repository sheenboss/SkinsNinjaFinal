package ggappsdev.Models;


public class ModelUser {

    public String country;
    public String email;
    public long loginDate;
    public String name;
    public String photo;
    public int points;
    public String refCode;
    public long signDate;
    public String status;
    public long dailyDate;
    public String referredBy;
    public int pointsReferral;
    public String steamUrl;
    public int ironPoints;

    public ModelUser() {


    }

    public ModelUser(String country, String email, long loginDate, String name, String photo, int points, String refCode, long signDate, String status, long dailyDate, String referredBy, int pointsReferral, String steamUrl, int ironPoints) {
        this.country = country;
        this.email = email;
        this.loginDate = loginDate;
        this.name = name;
        this.photo = photo;
        this.points = points;
        this.refCode = refCode;
        this.signDate = signDate;
        this.status = status;
        this.dailyDate = dailyDate;
        this.referredBy = referredBy;
        this.pointsReferral = pointsReferral;
        this.steamUrl = steamUrl;
        this.ironPoints = ironPoints;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(long loginDate) {
        this.loginDate = loginDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public long getSignDate() {
        return signDate;
    }

    public void setSignDate(long signDate) {
        this.signDate = signDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDailyDate() {
        return dailyDate;
    }

    public void setDailyDate(long dailyDate) {
        this.dailyDate = dailyDate;
    }

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    public int getPointsReferral() {
        return pointsReferral;
    }

    public void setPointsReferral(int pointsReferral) {
        this.pointsReferral = pointsReferral;
    }

    public String getSteamUrl() {
        return steamUrl;
    }

    public void setSteamUrl(String steamUrl) {
        this.steamUrl = steamUrl;
    }

    public int getIronPoints() {
        return ironPoints;
    }

    public void setIronPoints(int ironPoints) {
        this.ironPoints = ironPoints;
    }
}
