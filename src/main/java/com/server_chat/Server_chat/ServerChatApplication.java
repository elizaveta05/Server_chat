package com.server_chat.Server_chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.server_chat.Server_chat") // Указание базового пакета для сканирования компонентов
public class ServerChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerChatApplication.class, args);
	}

}
