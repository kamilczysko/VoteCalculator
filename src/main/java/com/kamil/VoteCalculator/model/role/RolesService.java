package com.kamil.VoteCalculator.model.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RolesService {

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
        for(Roles r : all)
            rolesMap.put(r.getUserRole(), r);

        return rolesMap;
    }

}
