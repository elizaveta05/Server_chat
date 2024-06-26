package com.server_chat.Server_chat.model;

public class ChatModel {
    private String chatId;
    private String userId;
    private String userLogin;
    private String userImage;
    private String lastMessage;

    public ChatModel(String chatId, String userId, String userLogin, String userImage, String lastMessage) {
        this.chatId = chatId;
        this.userId = userId;
        this.userLogin = userLogin;
        this.userImage = userImage;
        this.lastMessage = lastMessage;
    }

    public ChatModel() {
    }

    // Getters and setters for all fields, including lastMessage
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

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
