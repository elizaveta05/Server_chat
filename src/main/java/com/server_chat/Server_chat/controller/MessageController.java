package com.server_chat.Server_chat.controller;

import com.server_chat.Server_chat.model.ChatModel;
import com.server_chat.Server_chat.model.MessageModel;
import com.server_chat.Server_chat.model.UsersModel;
import com.server_chat.Server_chat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MessageController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    @Autowired
    public MessageController(SimpMessagingTemplate simpMessagingTemplate, MessageService messageService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
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
            // Возможно, отправка сообщения об ошибке на клиентскую сторону
            simpMessagingTemplate.convertAndSend("/topic/errors", e.getMessage());
        }
    }
    // Обработка запроса на получение всех сообщений между отправителем и получателем.
    @GetMapping("/fetchAllMessage/{senderId}/{recipientId}")
    public void getAllMessage(@DestinationVariable String senderId, @DestinationVariable String recipientId){
        System.out.println("Обработка запроса на получение сообщений для " + recipientId);
        List<MessageModel> messages = messageService.getAllMessages(senderId, recipientId);
        simpMessagingTemplate.convertAndSend("/topic/messages/" + recipientId, messages); // Отправка всех сообщений пользователю
    }

    @GetMapping("/getAllChatsForUser/{senderId}")
    public void getAllChatsForUser(@DestinationVariable String senderId) {
        try {
            if (senderId == null) {
                throw new IllegalArgumentException("SenderId is null");
            }

            System.out.println("Получение всех чатов пользователя " + senderId);
            List<ChatModel> chats = messageService.getAllChatsForUser(senderId);
            for (ChatModel chat : chats) {
                simpMessagingTemplate.convertAndSendToUser(senderId, "/topic/chats", chat);
            }
        } catch (Exception e) {
            e.printStackTrace();
            simpMessagingTemplate.convertAndSend("/topic/errors", e.getMessage());
        }
    }
}