package com.aymane.chatnojutsu.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "message")
public class Message {
    @Id
    private String messageId;
    private String roomId;
    private String messageFrom;
    private String messageTo;
    private String content;
    private Date createdAt;
}

