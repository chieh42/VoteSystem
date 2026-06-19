package com.example.vote.Service;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteService {

    private final JdbcTemplate jdbcTemplate;

    // 注入
    public VoteService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 查詢
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVoteResult() {
        return jdbcTemplate.queryForList("EXEC sp_get_vote_result");
    }

    // 投票
    @Transactional
    public void vote(String voter, Integer itemId) {
        jdbcTemplate.update(
            "EXEC sp_vote @voter=?, @item_id=?",
            voter, itemId
        );
    }

    // 新增
    @Transactional
    public void addItem(String itemName) {
        jdbcTemplate.update("EXEC sp_add_item @item_name = ?", itemName);
    }

    // 刪除
    @Transactional
    public void deleteItem(Integer itemId) {
        jdbcTemplate.update("EXEC sp_delete_item @item_id = ?", itemId);
    }

    // 更新
    @Transactional
    public void updateItem(Integer itemId, String newName) {
        try {
            jdbcTemplate.update("EXEC sp_update_item ?, ?", itemId, newName);
        } catch (Exception e) {
            throw new RuntimeException("更新項目失敗: " + itemId, e);
        }
    }
}