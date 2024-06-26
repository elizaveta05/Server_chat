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
import java.util.*;
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
    public void saveMessage(String senderId, String recipientId, String messageContent) {
        Firestore db = FirestoreClient.getFirestore();

        MessageModel message = new MessageModel();
        DocumentReference senderRef = db.collection("Users").document(senderId);
        DocumentReference recipientRef = db.collection("Users").document(recipientId);

        message.setSenderId(senderRef);
        message.setReceiverId(recipientRef);
        message.setContent(messageContent);
        message.setTimestamp(Instant.now());

        ApiFuture<DocumentReference> future = db.collection("Messages").add(message);
        try {
            DocumentReference document = future.get();
            logger.info("Message successfully saved. Message ID: " + document.getId());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error adding message", e);
        }
    }

    public List<MessageModel> getAllMessages(String senderId, String recipientId) {
        Firestore db = FirestoreClient.getFirestore();

        CollectionReference messagesCollection = db.collection("Messages");

        // Создаем условия для сообщений от senderId к recipientId
        Query querySent = messagesCollection.whereEqualTo("senderId", db.collection("Users").document(senderId))
                .whereEqualTo("receiverId", db.collection("Users").document(recipientId));

        // Создаем условия для сообщений от recipientId к senderId
        Query queryReceived = messagesCollection.whereEqualTo("senderId", db.collection("Users").document(recipientId))
                .whereEqualTo("receiverId", db.collection("Users").document(senderId));

        // Объединяем результаты обоих запросов
        Query query = db.collection("Messages").whereEqualTo("fakeField", "fakeValue"); // Это заглушка, так как Firestore не поддерживает OR оператор

        // Выполняем запрос, объединяя результаты сообщений от senderId к recipientId и от recipientId к senderId
        try {
            ApiFuture<QuerySnapshot> sentSnapshot = querySent.get();
            ApiFuture<QuerySnapshot> receivedSnapshot = queryReceived.get();

            List<QueryDocumentSnapshot> sentDocuments = sentSnapshot.get().getDocuments();
            List<QueryDocumentSnapshot> receivedDocuments = receivedSnapshot.get().getDocuments();

            List<MessageModel> allMessages = new ArrayList<>();

            // Добавляем все сообщения из обоих запросов в общий список
            for (QueryDocumentSnapshot document : sentDocuments) {
                MessageModel message = document.toObject(MessageModel.class);
                allMessages.add(message);
            }
            for (QueryDocumentSnapshot document : receivedDocuments) {
                MessageModel message = document.toObject(MessageModel.class);
                allMessages.add(message);
            }

            // Сортируем все сообщения по времени (опционально)
            Collections.sort(allMessages, new Comparator<MessageModel>() {
                @Override
                public int compare(MessageModel m1, MessageModel m2) {
                    return m1.getTimestamp().compareTo(m2.getTimestamp());
                }
            });

            if (allMessages.isEmpty()) {
                logger.warn("Нет сообщений между отправителем " + senderId + " и получателем " + recipientId);
            }

            return allMessages;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Ошибка при получении сообщений", e);
        }

        return new ArrayList<>(); // Возвращаем пустой список в случае ошибки
    }

    public List<ChatModel> getAllChatsForUser(String userId) {
        logger.info("Запрос от клиента на получение всех чатов для пользователя: " + userId);

        Firestore db = FirestoreClient.getFirestore();
        CollectionReference messagesCollection = db.collection("Messages");

        List<ChatModel> recipientChats = new ArrayList<>();
        HashSet<String> userIds = new HashSet<>();

        try {
            // Получаем все сообщения
            ApiFuture<QuerySnapshot> allMessagesQuerySnapshot = messagesCollection.get();

            // Обрабатываем результаты запроса для всех сообщений
            for (QueryDocumentSnapshot messageDocument : allMessagesQuerySnapshot.get().getDocuments()) {
                DocumentReference senderRef = (DocumentReference) messageDocument.get("senderId");
                DocumentReference receiverRef = (DocumentReference) messageDocument.get("receiverId");

                String senderId = senderRef.getId();
                String receiverId = receiverRef.getId();

                if (userId.equals(senderId) && !userIds.contains(receiverId)) {
                    userIds.add(receiverId);
                    DocumentSnapshot userDoc = receiverRef.get().get();

                    String lastMessage = getLastMessage(userId, receiverId);

                    ChatModel chat = new ChatModel(userDoc.getId(), receiverId, userDoc.getString("login"), userDoc.getString("photoUrl"), lastMessage);
                    recipientChats.add(chat);
                } else if (userId.equals(receiverId) && !userIds.contains(senderId)) {
                    userIds.add(senderId);
                    DocumentSnapshot userDoc = senderRef.get().get();

                    String lastMessage = getLastMessage(userId, senderId);

                    ChatModel chat = new ChatModel(userDoc.getId(), senderId, userDoc.getString("login"), userDoc.getString("photoUrl"), lastMessage);
                    recipientChats.add(chat);
                }
            }
            if (recipientChats.isEmpty()) {
                logger.info("У пользователя " + userId + " пока нет чатов.");
            } else {
                logger.info("Чаты для пользователя " + userId + " отправлены на клиент.");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Ошибка при получении чатов для пользователя", e);
        }

        return recipientChats;
    }

    private String getLastMessage(String userId, String otherUserId) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference messagesCollection = db.collection("Messages");

        Query query = messagesCollection
                .whereIn("senderId", Arrays.asList(
                        db.collection("Users").document(userId),
                        db.collection("Users").document(otherUserId)))
                .whereIn("receiverId", Arrays.asList(
                        db.collection("Users").document(userId),
                        db.collection("Users").document(otherUserId)))
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1);

        try {
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
            if (!documents.isEmpty()) {
                MessageModel lastMessage = documents.get(0).toObject(MessageModel.class);
                return lastMessage.getContent();
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Ошибка при получении последнего сообщения", e);
        }
        return "";
    }
    public UsersModel getUserByIdFromFirestore(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("Users").document(userId);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document;
        try {
            document = future.get();
            if (document.exists()) {
                return document.toObject(UsersModel.class);
            } else {
                logger.warn("Документ с userId = " + userId + " не найден");
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Ошибка при получении данных пользователя", e);
            return null;
        }
    }

}