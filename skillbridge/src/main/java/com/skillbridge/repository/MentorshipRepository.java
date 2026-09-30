package com.skillbridge.repository;

import com.skillbridge.entity.Mentorship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorshipRepository extends JpaRepository<Mentorship, Long> {

    List<Mentorship> findByMentorId(Long mentorId);

    List<Mentorship> findByStudentId(Long studentId);

    //List<Mentorship> findBySkillId(Long skillId);

    List<Mentorship> findByStatus(String status);
}
