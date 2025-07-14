package com.hazel.jaksim.calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarRepository calendarRepository;

    public List<Calendar> getTodosByDate(String today, String username) {
        return calendarRepository.findByDateBetween(today, username);
    }
}
