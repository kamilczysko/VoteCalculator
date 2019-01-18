package com.kamil.VoteCalculator.model.role;

import com.kamil.VoteCalculator.VoteCalculatorApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RolesService implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(RolesService.class);

    @Autowired
    RolesRepository rolesRepository;

    public Roles saveRole(Roles r) {
        return rolesRepository.save(r);
    }

    public List<Roles> saveRoles(List<Roles> roles) {
        return rolesRepository.saveAll(roles);
    }

    public List<Roles> getRoles() {
        return rolesRepository.findAll();
    }

    public Map<String, Roles> getRolesMap() {
        Map<String, Roles> rolesMap = new HashMap<>();
        List<Roles> all = rolesRepository.findAll();
        if(all.isEmpty())
            return null;
        for (Roles r : all)
            rolesMap.put(r.getUserRole(), r);

        return rolesMap;
    }

    private void initRoles() {
        Roles unvoted = new Roles();
        unvoted.setUserRole("unvoted");
        Roles voted = new Roles();
        voted.setUserRole("voted");
        saveRoles(Arrays.asList(voted, unvoted));
    }

    @Override
    public void run(String... args) throws Exception {
        if (VoteCalculatorApplication.firstRun) {
            logger.info("ROLES INIT");
            initRoles();
        }
    }
}
