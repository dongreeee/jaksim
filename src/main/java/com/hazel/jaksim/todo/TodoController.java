package com.hazel.jaksim.todo;

import com.hazel.jaksim.calendar.Calendar;
import com.hazel.jaksim.todo.dto.TodoCheckRequest;
import com.hazel.jaksim.websoket.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    private final TodoRepository todoRepository;

    @GetMapping("/todo/addView/{date}")
    public String addView(@PathVariable String date, Model model){
        model.addAttribute("date",date);
        return "todo_add.html";
    }

    @PostMapping("/todo/add")
    public String addTodo(String content,
                          String date,
                          Authentication auth){
        try{
            todoService.addTodo(content,date,auth.getName());
            return "redirect:/calendar?menu=active";
        }
        catch (Exception e){
            e.printStackTrace();  // 반드시 로그 찍어야 함!
            return "calendar";

        }
    }

    @GetMapping("/todo/info/{date}")
    @ResponseBody
    public List<Todo> getTodosByDate(@PathVariable String date, Authentication auth) {
        return todoService.getTodosByDate(auth.getName(), date);
    }

    @DeleteMapping("/todo/delete")
    ResponseEntity<String> DeleteTodo(@RequestBody Todo body){
        System.out.println(body.getId());
        todoRepository.deleteById(body.getId());
        return ResponseEntity.status(200).body("삭제완료");
    }

    @PostMapping("/todo/check")
    ResponseEntity<String> todoCheck(@RequestBody TodoCheckRequest request,
                                     Authentication auth){

        todoService.updateTodoCheck(auth.getName(), request);
        return ResponseEntity.ok().build();
//       http 응답코드 200 (OK)보내고 본문 body는 비워서 응답하는 코드
//       ResponseEntity : HTTP 응답 전체를 표현하는 Spring 클래스*상태코드, 헤더, 바디 포함)
//       OK() : 200 OK 상태 코드를 의미하는 정적 메서드
//       build() : 응답 본문 없이 빈 ResponseEntity 객체 생성
    }

}
