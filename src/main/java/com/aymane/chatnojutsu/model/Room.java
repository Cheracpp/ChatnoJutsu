package com.aymane.chatnojutsu.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "room")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "participants_idx", def = "{'messageFrom': 1, 'messageTo': 1}"),
    @CompoundIndex(name = "from_lastSent_idx", def = "{'messageFrom': 1, 'lastMessageSentAt': -1}"),
    @CompoundIndex(name = "to_lastSent_idx", def = "{'messageTo': 1, 'lastMessageSentAt': -1}")
})
public class Room {
    @Id
    private String id;
    private Date lastMessageSentAt;
    private String messageFrom;
    private String messageTo;
}
