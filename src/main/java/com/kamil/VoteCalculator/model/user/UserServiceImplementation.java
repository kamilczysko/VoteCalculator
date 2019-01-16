package com.kamil.VoteCalculator.model.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImplementation implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String pesel) throws UsernameNotFoundException {
        User user = userService.getUserByPesel(pesel);
        if (user == null)
            return null;
        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();

        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRoles().getUserRole()));

        org.springframework.security.core.userdetails.User userToLogin = new org.springframework.security.core.userdetails.User(
                user.id + "#" + user.firstName + " " + user.secondName, user.getPassword(), true, true, true, true, grantedAuthorities);

        return userToLogin;
    }
}
