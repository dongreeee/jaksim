package com.hazel.jaksim.map;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarMapRepository extends JpaRepository<CalendarMap,Long> {
    List<CalendarMap> findByCalendarId(Long calendarId);
}
