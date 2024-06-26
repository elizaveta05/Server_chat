package com.server_chat.Server_chat.controller;

import com.server_chat.Server_chat.model.UsersModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class UsersController {
    private final FirebaseController firebaseController = new FirebaseController();

    @GetMapping("/app/getUserById/{userId}")
    public UsersModel getUserById(@PathVariable String userId) {
        UsersModel user = firebaseController.getUserByIdFromFirestore(userId);
        return user;
    }
}