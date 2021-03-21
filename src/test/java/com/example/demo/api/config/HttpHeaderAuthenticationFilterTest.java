package com.example.demo.api.config;

import com.example.demo.service.user.UserService;
import com.example.demo.service.user.vo.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class HttpHeaderAuthenticationFilterTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private HttpHeaderAuthenticationFilter filter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    private final String HEADER_USER_ID = "X-USER-ID";

    @BeforeEach
    public void beforeEach() {
        SecurityContextHolder.setContext(securityContext);
        when(userService.findByUserId(eq(BigDecimal.ONE)))
                .thenReturn(Optional.of(new User(BigDecimal.ONE)));
    }

    @Test
    public void shouldNotSetAuthentication_whenDoFilter_givenEmptyXUserId() throws ServletException, IOException {
        // when
        filter.doFilterInternal(request, response, chain);

        // then
        verify(request).getHeader(HEADER_USER_ID);
        verify(securityContext, times(0)).setAuthentication(any());
    }

    @Test
    public void shouldNotSetAuthentication_whenDoFilter_givenXUserIdAndAuthenticated() throws ServletException, IOException {
        // given
        when(request.getHeader(eq(HEADER_USER_ID))).thenReturn("1");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // when
        filter.doFilterInternal(request, response, chain);

        // then
        verify(request).getHeader(HEADER_USER_ID);
        verify(securityContext, times(0)).setAuthentication(any());
    }

    @Test
    public void shouldSetAuthentication_whenDoFilter_givenXUserIdAndUnauthenticated() throws ServletException, IOException {
        // given
        when(request.getHeader(eq(HEADER_USER_ID))).thenReturn("1");

        // when
        filter.doFilterInternal(request, response, chain);

        // then
        verify(request).getHeader(HEADER_USER_ID);
        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        verify(securityContext).setAuthentication(captor.capture());
        User user = (User)captor.getValue().getPrincipal();
        assertEquals(BigDecimal.ONE, user.getUserId());
    }

}