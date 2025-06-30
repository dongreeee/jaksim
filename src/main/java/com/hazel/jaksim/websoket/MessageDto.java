package com.hazel.jaksim.websoket;

import lombok.Data;

@Data
public class MessageDto {

    private String content;
    private String sender;

    public MessageDto(String content, String sender) {
        this.content = content;
        this.sender = sender;
    }

}
