package com.mohamed.backend.repository.classinfo;

import com.mohamed.backend.dto.class_.AttendanceView;
import com.mohamed.backend.dto.topic.MainTopicView;
import com.mohamed.backend.model.classinfo.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    int countAttendanceBySessionId(int sessionId);

    List<AttendanceView> findBySessionId(int sessionId);

    @Query(value = """
                WITH distinct_subtopics AS (
                    SELECT DISTINCT ON (st1.id)
                           a.student_id,
                           mt.id AS main_topic_id,
                           mt.name AS main_topic_name,
                           st1.id AS sub_topic_id,
                           st1.name AS sub_topic_name
                    FROM attendance a
                    INNER JOIN session_topics st ON st.session_id = a.session_id
                    INNER JOIN sub_topics st1 ON st1.id = st.sub_topic_id
                    INNER JOIN main_topics mt ON mt.id = st1.main_topic_id
                    WHERE a.is_present = :isPresent
                      AND a.student_id = :studentId
                    ORDER BY st1.id, a.id
                )
                SELECT main_topic_id as id, main_topic_name AS name,
                       json_agg(json_build_object('id', sub_topic_id, 'name', sub_topic_name)) AS subTopics
                FROM distinct_subtopics
                GROUP BY main_topic_id, main_topic_name
                ORDER BY main_topic_id;
            """, nativeQuery = true)
    List<MainTopicView> findStudentTopics(@Param("studentId") Integer studentId, @Param("isPresent") boolean isPresent);

    @Query(value = """
            SELECT ROUND(
                COUNT(*) FILTER (WHERE is_present = true) * 100.0 / COUNT(*),
                2
            ) AS attendance_percentage
            FROM attendance
            WHERE student_id = :studentId
            GROUP BY student_id
            """, nativeQuery = true)
    Double getAttendancePercentageByStudentId(@Param("studentId") Integer studentId);
}
