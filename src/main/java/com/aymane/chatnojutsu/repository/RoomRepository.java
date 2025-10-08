package com.aymane.chatnojutsu.repository;

import com.aymane.chatnojutsu.model.Room;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface RoomRepository extends MongoRepository<Room, String> {

  @Query("{'participants': ?0}")
  List<Room> findByParticipantId(String userId, Sort sort);

  @Query("{ 'participants': { $all: ?0, $size: ?#{#participants.size()} } }")
  Optional<Room> findRoomWithExactParticipants(List<String> participants);

  @Query("{'_id' : ?0}")
  @Update("{$set : {'lastMessageSentAt' : ?1}}")
  void updateLastMessageSentAt(String roomId, Instant lastMessageSentAt);
}
