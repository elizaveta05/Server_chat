package com.server_chat.Server_chat.model;
import com.google.cloud.firestore.DocumentReference;
import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;

public class MessageModel {
    private String content;
    private DocumentReference senderId;
    private DocumentReference receiverId;
    private Instant timestamp;


    public MessageModel(String content, DocumentReference senderId, DocumentReference receiverId, Instant timestamp) {
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
    }

    public MessageModel() {

    }

    public DocumentReference getSenderId() {
        return senderId;
    }

    public void setSenderId(DocumentReference senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DocumentReference getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(DocumentReference receiverId) {
        this.receiverId = receiverId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

}