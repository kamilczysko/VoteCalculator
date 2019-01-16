package com.kamil.VoteCalculator.model.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;

    public User getUserByPesel(String pesel){
        return userRepo.findUserByPesel(pesel);
    }

    public void registerNewUser(){

    }

}
