package com.aymane.chatnojutsu.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "rooms")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(name = "userId_lastMessageSentAt", def = "{'participants' : 1, 'lastMessageSentAt' : -1}")
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
