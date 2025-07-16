package com.hazel.jaksim.map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MapController {

    private final MapRepository mapRepository;

    public Long addMap(){
        Long map_id = 0L;
        return map_id;
    }

}
