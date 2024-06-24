package com.server_chat.Server_chat.controller;

import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class FirebaseInitializer {

    private final FirebaseController firebaseController;

    public FirebaseInitializer(FirebaseController firebaseController) {
        this.firebaseController = firebaseController;
    }

    // Метод, выполняющийся после создания экземпляра класса.
    @PostConstruct
    public void init() {
        firebaseController.initFirebaseApp(); // Инициализация Firebase приложения.
    }
}