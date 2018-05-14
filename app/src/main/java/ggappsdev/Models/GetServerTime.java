package ggappsdev.Models;

public class GetServerTime {

   private Long currentTime;

   public GetServerTime(){

   }

    public GetServerTime(Long currentTime) {
        this.currentTime = currentTime;
    }

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }
}
