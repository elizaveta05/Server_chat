package com.server_chat.Server_chat.model;

public class ChatModel {
    private String chatId;
    private String userId;
    private String photoUrl;
    public ChatModel(String chatId, String userId, String photoUrl) {
        this.chatId = chatId;
        this.userId = userId;
        this.photoUrl = photoUrl;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImageResId() {
        return photoUrl;
    }

    public void setUserImageResId(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}