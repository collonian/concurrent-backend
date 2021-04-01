package com.example.demo.service.user;

import com.example.demo.service.user.vo.DemoUserDetails;
import com.example.demo.service.user.vo.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class DemoUserDetailsServiceTest {
    private final PreAuthenticatedAuthenticationToken authToken = new PreAuthenticatedAuthenticationToken("1", null);
    @Mock
    private UserService userService;
    @InjectMocks
    private DemoUserDetailsService demoUserDetailsService;

    @Test
    public void givenNormalUserId_whenLoadUserDetails_thenReturnUserDetails() {
        when(userService.findByUserId(any()))
                .thenReturn(Optional.of(new User(BigDecimal.ONE)));

        DemoUserDetails result = (DemoUserDetails) demoUserDetailsService.loadUserDetails(authToken);

        assertEquals(BigDecimal.ONE, result.getUser().getUserId());
    }

    @Test
    public void givenUnexistsUserId_whenLoadUserDetails_thenThrowUsernameNotFoundException() {
        when(userService.findByUserId(any()))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> demoUserDetailsService.loadUserDetails(authToken));
    }

}