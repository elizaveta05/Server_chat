package com.server_chat.Server_chat.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Конфигурационный класс для обмена сообщениями по протоколу WebSocket с использованием STOMP.
 */
@EnableWebSocketMessageBroker
@Configuration
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    /**
     * Регистрация конечных точек STOMP и разрешение использования SockJS для обеспечения совместимости с браузерами, не поддерживающими WebSocket.
     *
     * @param registry реестр конечных точек STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
                .setAllowedOrigins("*") // Указываем разрешенные источники (origins) для подключения к этой конечной точке. Здесь "*" разрешает подключения с любого источника.
                .withSockJS(); // Включаем поддержку SockJS, чтобы клиенты, не поддерживающие нативный WebSocket, также могли установить соединение.
        //WebSocketMessageBrokerConfigurer.super.registerStompEndpoints(registry);
    }

    /**
     * Конфигурация параметров брокера сообщений.
     *
     * @param registry реестр брокера сообщений
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // WebSocketMessageBrokerConfigurer.super.configureMessageBroker(registry);

        registry.setApplicationDestinationPrefixes("/app").enableSimpleBroker("/topic");
    }
}