package com.aymane.link.repository;

import com.aymane.link.model.Message;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {

  @Query("{ 'roomId': ?0}")
  Optional<List<Message>> findByRoomId(ObjectId roomId, Sort sort);
}
