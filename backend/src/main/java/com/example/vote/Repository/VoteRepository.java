package com.example.vote.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.vote.Entity.VoteItem;

@Repository
public interface VoteRepository extends JpaRepository<VoteItem, Integer> {

}