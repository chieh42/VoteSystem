package com.example.vote.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vote.Service.VoteService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/vote")
public class VoteController {

    private final VoteService voteService;

    // 💡 修正建構子：確保大括號內有將參數賦值給成員變數
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping("/result")
    public List<Map<String, Object>> getResult() {
        return voteService.getVoteResult();
    }

    @PostMapping("/vote")
    public void vote(
            // 改為非必填
            @RequestParam(required = false, defaultValue = "匿名") String voter,
            @RequestParam Integer itemId
    ) {
        voteService.vote(voter, itemId);
    }

    //新增投票項目 API
    @PostMapping("/item")
    public void addItem(@RequestParam String itemName) {
        voteService.addItem(itemName);
    }

    // 刪除投票項目 API
    @DeleteMapping("/item")
    public void deleteItem(@RequestParam Integer itemId) {
        voteService.deleteItem(itemId);
    }
}