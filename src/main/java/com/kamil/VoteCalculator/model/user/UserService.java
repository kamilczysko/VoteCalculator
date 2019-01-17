package com.kamil.VoteCalculator.model.user;

import com.kamil.VoteCalculator.model.role.Roles;
import com.kamil.VoteCalculator.model.role.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;
    @Autowired
    RolesService rolesService;

    public User getUserByPesel(String pesel) {
        return userRepo.findUserByPesel(pesel);
    }

    public User registerNewUser(User user) {
        return userRepo.save(user);
    }

    @Secured("unvoted")
    public void voted(long userId, boolean badVote) {
        Map<String, Roles> rolesMap = rolesService.getRolesMap();
        User user = userRepo.findById(userId).get();
        System.out.println(user);
        user.setRoles(rolesMap.get("voted"));
        user.setBadVote(badVote);
        userRepo.save(user);
    }

    @Secured("voted")
    public int getBadVotes(){
        return userRepo.getBadVotes();
    }

}
