package com.example.vote.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vote.Service.VoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/vote")
@Tag(name = "線上投票系統 API", description = "包含前台投票統計與 CRUD 操作")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping("/result")
    @Operation(summary = "獲取投票統計結果", description = "撈出所有投票項目及目前的累計總票數")
    public List<Map<String, Object>> getResult() {
        return voteService.getVoteResult();
    }

    @PostMapping("/vote")
    @Operation(summary = "執行使用者投票", description = "送出投票紀錄，自動綁定當前登入使用者")
    public void vote(
            Principal principal, // Spring Security 注入登入者的資訊
            @RequestParam Integer itemId) {

        // Token 解析用戶名
        String username = principal.getName();
        voteService.vote(username, itemId);
    }

    // 新增投票項目
    @PostMapping("/items/add")
    @Operation(summary = "新增投票項目", description = "建立新的投票選項，會檢查重複名稱")
    public void addItem(@RequestParam String itemName) {
        voteService.addItem(itemName);
    }

    // 更新既有投票項目
    @PutMapping("/items/update")
    @Operation(summary = "更新既有投票項目", description = "修改指定 ID 的項目名稱")
    public void updateItem(
            @Parameter(description = "要修改的項目 ID") @RequestParam Integer itemId,
            @Parameter(description = "全新項目名稱（例如：電競滑鼠）") @RequestParam String newName) {
        voteService.updateItem(itemId, newName);
    }

    // 刪除投票項目
    @DeleteMapping("/items/delete")
    @Operation(summary = "刪除既有投票項目", description = "刪除指定 ID 的投票項目，會連同相關投票紀錄一併清除")
    public void deleteItem(@Parameter(description = "要刪除的項目 ID") @RequestParam Integer itemId) {
        voteService.deleteItem(itemId);
    }
}