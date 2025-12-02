package com.aymane.link.repository;

import com.aymane.link.model.Room;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RoomRepository extends MongoRepository<Room, ObjectId> {

  @Query("{ 'participants': { $all: ?0, $size: ?#{#participants.size()} } }")
  Optional<Room> findRoomWithExactParticipants(List<String> participants);

  @Aggregation(pipeline = {"{ $match: { 'participants': ?0 } }", """
      {
        $lookup: {
          from: 'messages',
          let: { roomId: '$_id' },
          pipeline: [
            { $match: { $expr: { $eq: ['$roomId', '$$roomId'] } } },
            { $sort: { timestamp: -1 } },
            { $limit: 1 }
          ],
          as: 'lastMessage'
        }
      }
      """, "{ $unwind: { path: '$lastMessage', preserveNullAndEmptyArrays: false } }",
      "{ $sort: { 'lastMessage.timestamp': -1 } }"})
  List<Room> findByParticipantIdOrderedByLastMessage(String userId);

  boolean existsByRoomIdAndParticipantsContaining(ObjectId id, String userId);
}
