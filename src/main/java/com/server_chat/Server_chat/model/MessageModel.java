package com.server_chat.Server_chat.model;
import java.time.LocalDateTime;

public class MessageModel {
    private String content;
    private String senderId;
    private String receiverId;
    private LocalDateTime timestamp;
    private boolean status;

    public MessageModel(String content, String senderId, String receiverId, LocalDateTime timestamp) {
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.status = false;
    }

    public MessageModel() {

    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}