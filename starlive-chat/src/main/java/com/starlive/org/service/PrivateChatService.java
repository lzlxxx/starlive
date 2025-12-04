package com.starlive.org.service;

import com.starlive.org.model.ChatMessage;
import java.util.List;

public interface PrivateChatService {
    void sendMessage(ChatMessage message);
} 