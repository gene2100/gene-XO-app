package com.gene.tictactoe.model;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tictactoe_user")
public class TicTacToeUser {
    
    @Id
    private String id;
    private String name;
    private Integer point;
    private Integer conWin;
    private LocalDateTime lastLogin;
    private LocalDateTime lastMatch;
}
