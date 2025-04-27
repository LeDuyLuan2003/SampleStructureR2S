package com.r2s.ApiWebReview.controller;

import com.r2s.ApiWebReview.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {


     @GetMapping()
     public ResponseEntity<String> getAdminData() {
         return ResponseEntity.ok("Admin data");
     }


}
