package org.ofz.offset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OffsetController {
    private OffsetService offsetService;

    @Autowired
    public OffsetController(OffsetService offsetService) {
        this.offsetService = offsetService;
    }

    @GetMapping("/schedule/offset/{userId}")
    public ResponseEntity<Void> processOffset(@PathVariable(value = "userId") Long userId){
        offsetService.processOffset(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
