package at.wrk.coceso.repository;

import at.wrk.coceso.entity.ReceivedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<ReceivedMessage, Long> {

    @Query("SELECT m FROM ReceivedMessage m WHERE m.timestamp >= ?1 ORDER BY m.timestamp ASC")
    List<ReceivedMessage> findReceivedAfter(Instant timestamp);
}
