package com.aymane.chatnojutsu.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
    private String roomId;
    private String[] participants;
    private String type;
    @LastModifiedDate
    private Date lastMessageSentAt;
    @CreatedDate
    private Date createdAt;
}
