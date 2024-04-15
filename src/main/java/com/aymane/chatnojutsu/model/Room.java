package com.aymane.chatnojutsu.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "room")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @Id
    private String id;
    private Date lastMessageSentAt;
    private String messageFrom;
    private String messageTo;
}
