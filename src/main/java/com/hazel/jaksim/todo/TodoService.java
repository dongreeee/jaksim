package com.hazel.jaksim.todo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public List<Todo> getTodosByDate(String username, String date) {
        return todoRepository.findByUsernameAndDateymd(username, date);
    }
}
