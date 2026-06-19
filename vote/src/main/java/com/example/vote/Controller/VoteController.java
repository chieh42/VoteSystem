package com.example.vote.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.vote.Service.VoteService;

import java.util.List;
import java.util.Map;

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
            @RequestParam String voter,
            @RequestParam Integer itemId
    ) {
        voteService.vote(voter, itemId);
    }
}