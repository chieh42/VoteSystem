package com.example.vote.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Vote_Record")
@Data
public class VoteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "voter_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private VoteItem voteItem;

    @Column(insertable = false, updatable = false)
    private LocalDateTime voteTime;
}