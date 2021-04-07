package com.example.demo.service.user;

import com.example.demo.service.user.vo.DemoUserDetails;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DemoUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
    private final UserService userService;

    public DemoUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        String principal = token.getPrincipal().toString();
        BigDecimal userId = new BigDecimal(principal);

        return userService.findByUserId(userId)
                .map(DemoUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Failed to find " + principal));
    }
}
