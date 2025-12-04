package com.starlive.org.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageVo {
private List<ChatMessage> messages;
private int total;
private int page;
private int pageSize;
private boolean hasMore;


public ChatMessageVo(List<ChatMessage> messages, int total, int page, int pageSize) {
    this.messages = messages;
    this.total = total;
    this.page = page;
    this.pageSize = pageSize;
    this.hasMore = total > (page + 1) * pageSize;
}
}
