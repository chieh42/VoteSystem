package com.example.vote.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.vote.Entity.User;
import com.example.vote.Repository.UserRepository;
import com.example.vote.Security.JwtTokenProvider;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    // 處理會員註冊
    public String register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("錯誤：該帳號已被註冊！");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("user");

        userRepository.save(user);
        return "註冊成功！";
    }

    // 處理會員登入
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("錯誤：找不到該使用者！"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("錯誤：密碼不正確！");
        }

        return tokenProvider.generateToken(user.getUsername(), user.getRole());
    }

    // AuthController 可以透過帳號撈出完整的 User 資料
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("錯誤：找不到該使用者！"));
    }
}