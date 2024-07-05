package com.server_chat.Server_chat.controller;

import com.server_chat.Server_chat.model.ChatModel;
import com.server_chat.Server_chat.model.MessageModel;
import com.server_chat.Server_chat.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MessageController {
    private final MessageService messageService;
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);


    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    // Обработка отправки сообщения от отправителя к получателю
    @PostMapping("/chat/{senderId}/{recipientId}/{message}")
    public void sendMessage(@PathVariable String senderId, @PathVariable String recipientId, @RequestBody String message) {
        try {
            System.out.println("Обработка отправки сообщения " + message + " для " + recipientId);
            messageService.sendMessageToUser(senderId, recipientId, message);
        } catch (Exception e) {
            // Логирование ошибки
            e.printStackTrace();
        }
    }
    // Обработка запроса на получение всех сообщений между отправителем и получателем.
    @GetMapping("/app/fetchAllMessage/{senderId}/{recipientId}")
    public List<MessageModel> getAllMessage(@PathVariable String senderId, @PathVariable String recipientId){
        if (senderId == null || recipientId==null) {
            throw new IllegalArgumentException("Parameters is null");
        }
        System.out.println("Обработка запроса на получение сообщений для " + recipientId);
        List<MessageModel> messages = messageService.getAllMessages(senderId, recipientId);
        return messages;
    }
    // Обработка запроса на получение всех чатов для пользователя
    @GetMapping("/app/getAllChatsForUser/{senderId}")
    public List<ChatModel> getAllChatsForUserHttp(@PathVariable String senderId) {
        try {
            if (senderId == null) {
                throw new IllegalArgumentException("SenderId is null");
            }

            System.out.println("Получение всех чатов пользователя " + senderId);
            List<ChatModel> chats = messageService.getAllChatsForUser(senderId);
            System.out.println("Получение всех чатов пользователя " + chats);

            return chats;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}