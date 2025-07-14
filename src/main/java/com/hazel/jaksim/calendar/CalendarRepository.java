package com.hazel.jaksim.calendar;

import com.hazel.jaksim.member.Member;
import com.hazel.jaksim.todo.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findByUsername(String username);

    @Query("SELECT e FROM Calendar e WHERE :today BETWEEN e.sdate AND e.edate" +
            " AND e.username = :username ")
    List<Calendar> findByDateBetween(@Param("today") String today, @Param("username") String username);

}
