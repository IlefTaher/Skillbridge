package com.skillbridge.repository;

import com.skillbridge.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMentorId(Long mentorId);

    List<Review> findByReviewerId(Long reviewerId);
}
