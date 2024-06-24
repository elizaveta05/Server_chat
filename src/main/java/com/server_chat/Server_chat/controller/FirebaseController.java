package com.server_chat.Server_chat.controller;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.protobuf.Timestamp;
import com.server_chat.Server_chat.model.ChatModel;
import com.server_chat.Server_chat.model.MessageModel;
import com.server_chat.Server_chat.model.UsersModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class FirebaseController {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseController.class);

    // Инициализация Firebase Admin SDK для взаимодействия с Firestore.
    public void initFirebaseApp() {
        try {
            FileInputStream serviceAccount = new FileInputStream("C:\\Users\\elozo\\messenger-7c266-firebase-adminsdk-25370-025cc4f94b.json");

            // Создание параметров конфигурации Firebase.
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://messenger-7c266-default-rtdb.firebaseio.com/")
                    .build();

            // Проверка наличия инициализированного FirebaseApp перед инициализацией.
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase Admin SDK успешно проинициализирован");
            }
        } catch (IOException e) {
            logger.error("Ошибка инициализации Firebase Admin SDK", e);
        }
    }

    // Сохранение сообщения в Firestore
    public void saveMessage(String senderId, String recipientId, String messageContent) {
        Firestore db = FirestoreClient.getFirestore();

        MessageModel message = new MessageModel();
        message.setSenderId(senderId);
        message.setReceiverId(recipientId);
        message.setContent(messageContent);
        message.setTimestamp(LocalDateTime.now());
        message.setStatus(false);

        // Создание нового документа в коллекции "Messages" и сохранение сообщения
        DocumentReference docRef = db.collection("Messages").document();
        ApiFuture<WriteResult> result = docRef.set(message);
        try {
            WriteResult writeResult = result.get();
            logger.info("Сообщение успешно сохранено. Время обновления: " + writeResult.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Ошибка добавления сообщения", e);
        }
    }

    // Получение всех сообщений между отправителем и получателем.
    public List<MessageModel> getAllMessages(String senderId, String recipientId) {
        Firestore db = FirestoreClient.getFirestore();

        CollectionReference messagesCollection = db.collection("Messages");

        // Формирование запроса для получения сообщений между отправителем и получателем.
        Query query = messagesCollection.whereEqualTo("senderId", senderId)
                .whereEqualTo("recipientId", recipientId)
                .orderBy("timestamp", Query.Direction.ASCENDING);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<MessageModel> messages = new ArrayList<>();

        try {
            // Извлечение документов из результата запроса и добавление их в список сообщений.
            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
                MessageModel message = document.toObject(MessageModel.class);
                messages.add(message);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Ошибка при получении сообщений", e);
        }

        return messages;
    }
    // Получение всех возможных пользователей, с которыми взаимодействовал отправитель.
    public List<ChatModel> getAllChatsForUser(String senderId) {
        logger.info("Получен запрос от клиента на получение всех чатов для пользователя " + senderId);

        Firestore db = FirestoreClient.getFirestore();

        CollectionReference usersCollection = db.collection("Users");
        CollectionReference messagesCollection = db.collection("Messages");

        // Формирование запроса для получения всех чатов для указанного отправителя.
        Query query = messagesCollection.whereEqualTo("senderId", senderId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<ChatModel> recipientChats = new ArrayList<>();

        try {
            // Итерация по результатам запроса для поиска получателей сообщений.
            if (querySnapshot.get().isEmpty()) {
                // Если у пользователя нет чатов, вывести сообщение в лог
                logger.info("У пользователя " + senderId + " пока нет чатов.");
            } else {
                for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
                    String recipientId = document.getString("recipientId");

                    // Получение информации о чате-получателе из коллекции "Chats".
                    DocumentReference chatRef = usersCollection.document(recipientId);
                    ApiFuture<DocumentSnapshot> chatSnapshot = chatRef.get();
                    DocumentSnapshot chatDocument = chatSnapshot.get();

                    if (chatDocument.exists()) {
                        // Получение данных о чате и создание объекта ChatModel
                        ChatModel chat = chatDocument.toObject(ChatModel.class);
                        recipientChats.add(chat);
                    } else {
                        logger.warn("Чат с идентификатором " + recipientId + " не существует в коллекции Chats.");
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Ошибка при получении чатов для пользователя", e);
        }

        return recipientChats;
    }
}