package com.r2s.ApiWebReview.controller;

import com.r2s.ApiWebReview.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {


    @GetMapping()
    public ResponseEntity<String> getUserData() {
        return ResponseEntity.ok("User data");
    }

}
