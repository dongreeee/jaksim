package com.hazel.jaksim.calendar;

import com.hazel.jaksim.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findByUsername(String username);
}
