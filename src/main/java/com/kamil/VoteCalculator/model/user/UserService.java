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
    private UserRepository userRepo;
    @Autowired
    private RolesService rolesService;

    public User getUserByPesel(String pesel) {
        return userRepo.findUserByPesel(pesel);
    }

    public User registerNewUser( User user) {
        return userRepo.save(user);
    }

    @Secured("unvoted")
    public User voted(long userId, boolean badVote) {
        Map<String, Roles> rolesMap = rolesService.getRolesMap();
        User user = userRepo.findById(userId).get();

        if (badVote || user.isDisallowed())
            user.setVoidedVote(true);

        user.setRoles(rolesMap.get("voted"));
        userRepo.save(user);

        return user;
    }

    @Secured("voted")
    public int getBadVotes() {
        Integer voidedVotes = userRepo.getVoidedVotes();
        if (voidedVotes == null)
            return 0;
        return voidedVotes.intValue();
    }

    @Secured("voted")
    public int getDisallowedVotes() {
        Integer disallowedVotes = userRepo.getDisallowedVotes();
        if (disallowedVotes == null)
            return 0;
        return disallowedVotes.intValue();
    }
}
