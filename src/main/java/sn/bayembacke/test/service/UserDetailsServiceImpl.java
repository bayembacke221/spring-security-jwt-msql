package sn.bayembacke.test.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sn.bayembacke.test.be.Users;
import sn.bayembacke.test.be.UserEntity;
import sn.bayembacke.test.dao.UserRepository;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    UserDetailsService userDetails;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = userRepository.findByUsername(username);
        user.orElseThrow(()->new UsernameNotFoundException("Username not found" + username));

        return user.map(UserEntity::new).get();
    }
}
