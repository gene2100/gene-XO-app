package com.gene.tictactoe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gene.tictactoe.model.TicTacToeUser;

public interface TicTacToeUserRepository extends JpaRepository<TicTacToeUser, String> {
}
