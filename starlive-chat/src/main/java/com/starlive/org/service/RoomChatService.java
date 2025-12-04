package com.starlive.org.service;

import com.starlive.org.model.ChatMessage;
import java.util.List;
import java.util.Set;

public interface RoomChatService {
    void sendMessage(ChatMessage message);
    //void joinRoom(String userId, String roomId);
    void leaveRoom(String userId, String roomId);
} 