package com.example.vote.Service;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteService {

    private final JdbcTemplate jdbcTemplate;

    public VoteService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getVoteResult() {
        return jdbcTemplate.queryForList("EXEC sp_get_vote_result");
    }

    @Transactional
    public void vote(String voter, Integer itemId) {
        jdbcTemplate.update(
            "EXEC sp_vote @voter=?, @item_id=?",
            voter, itemId
        );
    }

    @org.springframework.transaction.annotation.Transactional
    public void addItem(String itemName) {
        // 新增項目
        jdbcTemplate.update("EXEC sp_add_item @item_name = ?", itemName);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteItem(Integer itemId) {
        // 刪除項目
        jdbcTemplate.update("EXEC sp_delete_item @item_id = ?", itemId);
    }

    // 更新既有項目
    @Transactional
    public void updateItem(Integer itemId, String newName) {
        try {
            jdbcTemplate.update("EXEC sp_update_item ?, ?", itemId, newName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}