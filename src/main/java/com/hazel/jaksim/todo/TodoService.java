package com.hazel.jaksim.todo;

import com.hazel.jaksim.todo.dto.TodoCheckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public void addTodo(String content,
                        String date,
                        String username){
//        System.out.println(content);
//        System.out.println(date);

        Todo todo = new Todo();
        todo.setContent(content);
        todo.setUsername(username);
        todo.setCategory("work");
        todo.setDateymd(date);
        todo.setIsCompleted("0");
        todoRepository.save(todo);
    }

    public List<Todo> getTodosByDate(String username, String date) {
        return todoRepository.findByUsernameAndDateymd(username, date);
    }

    public void updateTodoCheck(String username, TodoCheckRequest request){
        Todo todo = todoRepository.findById(request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!todo.getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        todo.setIsCompleted(request.isChecked() ? "1" : "0");
        todo.setRegdateUpdate(LocalDateTime.now());
        todoRepository.save(todo);

    }
}
