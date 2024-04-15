package com.aymane.chatnojutsu.repository;

import com.aymane.chatnojutsu.model.Message;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, String> {
    @Query("{ 'roomId': ?0}")
    Optional<List<Message>> findByRoomId(String roomId, Sort sort);
}
