package com.example.vote.Controller;

import com.example.vote.Service.AuthService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // 允許前端 Vue 跨域呼叫
public class AuthController {

    @Autowired
    private AuthService authService;

    // 1. 會員註冊接口
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        try {
            String message = authService.register(request.getUsername(), request.getPassword());
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. 會員登入接口（同時回傳 Token 與 該帳號的真實 Role 權限）
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // 驗證帳密並取得 JWT Token
            String token = authService.login(request.getUsername(), request.getPassword());

            // 透過服務層動態查詢該使用者在資料庫中的真實角色 (admin 或 user)
            com.example.vote.Entity.User user = authService.getUserByUsername(request.getUsername());

            // 打包成 JSON 回傳給前端 Vue
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("tokenType", "Bearer");
            response.put("role", user.getRole()); // 動態返回角色，不再由前端寫死！

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

// DTO 類別：用來接收前端傳遞過來的 JSON 帳密格式
@Data
class AuthRequest {
    private String username;
    private String password;
}