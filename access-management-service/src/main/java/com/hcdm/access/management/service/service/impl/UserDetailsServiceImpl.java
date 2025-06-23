package com.hcdm.access.management.service.service.impl;



import com.hcdm.access.management.service.entity.AppUser;
import com.hcdm.access.management.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            System.out.println(user.get().getPassword());
            System.out.println(user.get().getUsername());
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.get().getRole().name());

            return new org.springframework.security.core.userdetails.User(
                    user.get().getUsername(), user.get().getPassword(), Collections.singleton(authority));
        }
        throw new UsernameNotFoundException("AppUser not found with username: " + username);

    }
}
