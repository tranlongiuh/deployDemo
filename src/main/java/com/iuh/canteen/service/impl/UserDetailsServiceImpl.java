package com.iuh.canteen.service.impl;

import com.iuh.canteen.entity.User;
import com.iuh.canteen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * loadUserByUsername Lấy dữ liệu Tài khoản theo Tên Tài khoản
     *
     * @param userName Tên Tài khoản
     * @return UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(userName);
        // Trả về đối tượng User với quyền hạn
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities() // Trả về quyền hạn ở đây
        );
    }
}

