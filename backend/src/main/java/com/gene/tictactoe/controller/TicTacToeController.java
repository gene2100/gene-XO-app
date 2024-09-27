package com.gene.tictactoe.controller;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gene.tictactoe.model.TicTacToeUser;
import com.gene.tictactoe.repository.TicTacToeUserRepository;

@RestController
@CrossOrigin
@RequestMapping("/tictactoe/api/v1")
public class TicTacToeController {
    
    @Autowired
    private TicTacToeUserRepository tictactoeUserRepo;

    @GetMapping(value = "/user")
    @ResponseStatus(HttpStatus.OK)
    public List<TicTacToeUser> getAllUser(){
        return tictactoeUserRepo.findAll(Sort.by(Sort.Direction.DESC, "point"));
    }

    @GetMapping(value = "/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TicTacToeUser getUser(@PathVariable(value = "id") String userId){
        return tictactoeUserRepo.findById(userId).get();
    }

    @PostMapping(value = "/user")
    public ResponseEntity<TicTacToeUser> createOrUpdateUserLogin(@RequestBody TicTacToeUser user) {
        if(tictactoeUserRepo.existsById(user.getId())){
            TicTacToeUser userData = tictactoeUserRepo.findById(user.getId()).get();
            userData.setLastLogin(LocalDateTime.now());
            return ResponseEntity.ok().body(tictactoeUserRepo.save(userData));
        }else{
            user.setConWin(0);
            user.setPoint(0);
            user.setLastLogin(LocalDateTime.now());
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(tictactoeUserRepo.save(user));
        }
    }

    @PutMapping(value = "/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateScore(@RequestBody TicTacToeUser userData){
        TicTacToeUser user = tictactoeUserRepo.findById(userData.getId()).get();
        user.setPoint(userData.getPoint());
        user.setConWin(userData.getConWin());
        user.setLastMatch(LocalDateTime.now());
        tictactoeUserRepo.save(user);
        return;
    }
}
