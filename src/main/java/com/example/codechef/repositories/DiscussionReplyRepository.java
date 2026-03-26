package com.example.codechef.repositories;

import com.example.codechef.models.DiscussionReply;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiscussionReplyRepository extends JpaRepository<DiscussionReply, Long> {
    List<DiscussionReply> findByDiscussionIdOrderByCreatedAtAsc(Long discussionId);
}
