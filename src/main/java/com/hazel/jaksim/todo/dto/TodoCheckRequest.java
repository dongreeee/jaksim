package com.hazel.jaksim.todo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoCheckRequest {
    private Long id;
    private boolean checked;
}
