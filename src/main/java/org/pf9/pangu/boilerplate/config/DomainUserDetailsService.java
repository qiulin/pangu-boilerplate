package org.pf9.pangu.boilerplate.config;

import org.pf9.pangu.boilerplate.entity.User;
import org.pf9.pangu.boilerplate.service.AuthorityService;
import org.pf9.pangu.boilerplate.service.UserService;
import org.pf9.pangu.framework.auth.security.UserNotActivatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserService userService;

    private final AuthorityService authorityService;

    public DomainUserDetailsService(UserService userService,
                                    AuthorityService authorityService) {

        this.userService = userService;
        this.authorityService = authorityService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        Optional<User> userFromDatabase = userService.findUserByLogin(lowercaseLogin);
        return userFromDatabase.map(user -> {
            if (!user.getActivated()) {
                throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
            }
            //EngineAsscessDecisionManager 权限校验会使用到这个集合 登陆时将用户权限加载在内存中进行判定
            List<GrantedAuthority> grantedAuthorities = authorityService.findAuthoritiesByUserId(user.getId()).stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getCode()))
                    .collect(Collectors.toList());
            return new org.springframework.security.core.userdetails.User(lowercaseLogin,
                            user.getPassword(),
                    grantedAuthorities);
        }).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " +
                "database"));
    }
}
