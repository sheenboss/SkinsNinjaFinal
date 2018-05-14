package ggappsdev.Models;

import java.util.Date;

public class ChatModel {

    private String messageText;
    private String messageUser;
    private String messagePhoto;
    private long messageTime;

    public ChatModel(){

    }

    public ChatModel(String messageText, String messageUser, String messagePhoto) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messagePhoto = messagePhoto;

        messageTime = new Date().getTime();
    }

    public ChatModel(String messageText, String messageUser, String messagePhoto, long messageTime) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messagePhoto = messagePhoto;

        messageTime = new Date().getTime();
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessagePhoto() {
        return messagePhoto;
    }

    public void setMessagePhoto(String messagePhoto) {
        this.messagePhoto = messagePhoto;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
