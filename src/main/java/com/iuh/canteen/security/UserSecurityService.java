package com.iuh.canteen.security;

import com.iuh.canteen.entity.User;
import com.iuh.canteen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService {

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User returnedUser = userRepository.findByUsername(username);
        if (returnedUser == null) {
            throw new UsernameNotFoundException("USERNAME NOT FOUND");
        }
        return new org.springframework.security.core.userdetails.User(
                returnedUser.getUsername(),
                returnedUser.getPassword(),
                returnedUser.getAuthorities()
        );
    }
}
