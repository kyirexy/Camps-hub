package com.liuxy.campushub.service;

import com.liuxy.campushub.entity.StudentUser;
import com.liuxy.campushub.mapper.StudentUserMapper;
import com.liuxy.campushub.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private StudentUserMapper studentUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StudentUser user = studentUserMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Convert user role to string format
        String role = "ROLE_USER"; // Default role
        if (user.getUserRole() == 1) {
            role = "ROLE_ADMIN";
        }

        return UserDetailsImpl.build(
            user.getUserId(),
            user.getUsername(),
            user.getPassword(),
            user.getEmail(),
            Arrays.asList(role)
        );
    }
}