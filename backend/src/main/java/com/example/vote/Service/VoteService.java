package com.example.vote.Service;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vote.Entity.User;
import com.example.vote.Repository.UserRepository;

@Service
public class VoteService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository; // 引入 UserRepository 查詢使用者

    // 建構子注入
    public VoteService(JdbcTemplate jdbcTemplate, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    public List<Map<String, Object>> getVoteResult() {
        return jdbcTemplate.queryForList("EXEC sp_get_vote_result");
    }

    @Transactional
    public void vote(String username, Integer itemId) {
        // 1. 透過帳號查出使用者實體
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("錯誤：找不到該投票使用者！"));

        // 2. 將 user.getId() 帶入修改後的 sp_vote 預存程序
        try {
            jdbcTemplate.update(
                "EXEC sp_vote @user_id=?, @item_id=?",
                user.getId(), itemId
            );
        } catch (Exception e) {
            // 捕捉資料庫拋出的錯誤（例如：已投過此項目的 RAISERROR）
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void addItem(String itemName) {
        try {
            jdbcTemplate.update("EXEC sp_add_item @item_name = ?", itemName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void deleteItem(Integer itemId) {
        try {
            jdbcTemplate.update("EXEC sp_delete_item @item_id = ?", itemId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void updateItem(Integer itemId, String newName) {
        try {
            jdbcTemplate.update("EXEC sp_update_item ?, ?", itemId, newName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}