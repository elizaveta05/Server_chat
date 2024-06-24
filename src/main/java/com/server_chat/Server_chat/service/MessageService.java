package com.server_chat.Server_chat.service;

import com.server_chat.Server_chat.controller.FirebaseController;
import com.server_chat.Server_chat.model.ChatModel;
import com.server_chat.Server_chat.model.MessageModel;
import com.server_chat.Server_chat.model.UsersModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private final FirebaseController firebaseController;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public MessageService(FirebaseController firebaseController, SimpMessagingTemplate simpMessagingTemplate) {
        this.firebaseController = firebaseController;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    // Отправка сообщения пользователю
    public void sendMessageToUser(String senderId, String recipientId, String message) {
        simpMessagingTemplate.convertAndSendToUser(recipientId, "/topic/messages", message);
        firebaseController.saveMessage(senderId, recipientId, message);
    }

    // Получение всех сообщений между отправителем и получателем.
    public List<MessageModel> getAllMessages(String senderId, String recipientId) {
        List<MessageModel> messages = firebaseController.getAllMessages(senderId, recipientId);
        return messages;
    }

    // Получение всех чатов для пользователя.
    public List<ChatModel> getAllChatsForUser(String senderId) {
        List<ChatModel> recipientIds = firebaseController.getAllChatsForUser(senderId);
        return recipientIds;
    }
}