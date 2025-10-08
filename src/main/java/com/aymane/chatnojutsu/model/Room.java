package com.aymane.chatnojutsu.model;

import java.time.Instant;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "rooms")
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
    private String name;
    @LastModifiedDate
    private Instant lastMessageSentAt;
    @CreatedDate
    private Instant createdAt;
}
