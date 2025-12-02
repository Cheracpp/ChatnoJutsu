package com.aymane.link.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "messages")
@CompoundIndex(name = "room_created_idx", def = "{'roomId': 1, 'timestamp': -1}")
public class Message {

  @Id
  private ObjectId messageId;
  private ObjectId roomId;
  private String senderId;
  private String content;
  @CreatedDate
  private Instant timestamp;
}

