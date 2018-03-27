package org.pf9.pangu.boilerplate.security;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.pf9.pangu.boilerplate.entity.PersistentToken;
import org.pf9.pangu.boilerplate.service.PersistentTokenService;
import org.pf9.pangu.boilerplate.service.UserService;
import org.pf9.pangu.framework.common.config.PanguProperties;
import org.pf9.pangu.framework.common.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Custom implementation of Spring Security's RememberMeServices.
 * <p>
 * Persistent tokens are used by Spring Security to automatically log in users.
 * <p>
 * This is a specific implementation of Spring Security's remember-me authentication, but it is much
 * more powerful than the standard implementations:
 * <ul>
 * <li>It allows a user to see the list of his currently opened sessions, and invalidate them</li>
 * <li>It stores more information, such as the IP address and the user agent, for audit purposes<li>
 * <li>When a user logs out, only his current session is invalidated, and not all of his sessions</li>
 * </ul>
 * <p>
 * Please note that it allows the use of the same token for 5 seconds, and this value stored in a specific
 * cache during that period. This is to allow concurrent requests from the same user: otherwise, two
 * requests being sent at the same time could invalidate each other's token.
 * <p>
 * This is inspired by:
 * <ul>
 * <li><a href="http://jaspan.com/improved_persistent_login_cookie_best_practice">Improved Persistent Login Cookie
 * Best Practice</a></li>
 * <li><a href="https://github.com/blog/1661-modeling-your-app-s-user-session">GitHub's "Modeling your App's User Session"</a></li>
 * </ul>
 * <p>
 * The main algorithm comes from Spring Security's PersistentTokenBasedRememberMeServices, but this class
 * couldn't be cleanly extended.
 */
@Service
public class PersistentTokenRememberMeServices extends
        AbstractRememberMeServices {

    private final Logger log = LoggerFactory.getLogger(PersistentTokenRememberMeServices.class);

    // Token is valid for one month
    private static final int TOKEN_VALIDITY_DAYS = 31;

    private static final int TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * TOKEN_VALIDITY_DAYS;

    private static final int UPGRADED_TOKEN_VALIDITY_SECONDS = 5;

    private Cache<String, UpgradedRememberMeToken> upgradedTokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(UPGRADED_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS)
            .build();

    //    private final PersistentTokenRepository persistentTokenRepository;
    private final PersistentTokenService persistentTokenService;
//    private final PersistentTokenMapper persistentTokenMapper;

    //    private final UserRepository userRepository;
    private final UserService userService;

    public PersistentTokenRememberMeServices(PanguProperties panguProperties,
                                             org.springframework.security.core.userdetails.UserDetailsService userDetailsService,
                                             PersistentTokenService persistentTokenService, UserService userService) {

        super(panguProperties.getSecurity().getRememberMe().getKey(), userDetailsService);
        this.persistentTokenService = persistentTokenService;
        this.userService = userService;
//        this.persistentTokenMapper = persistentTokenMapper;
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
                                                 HttpServletResponse response) {

        synchronized (this) { // prevent 2 authentication requests from the same user in parallel
            String login = null;
            UpgradedRememberMeToken upgradedToken = upgradedTokenCache.getIfPresent(cookieTokens[0]);
            if (upgradedToken != null) {
                login = upgradedToken.getUserLoginIfValidAndRecentUpgrade(cookieTokens);
                log.debug("Detected previously upgraded login token for user '{}'", login);
            }

            if (login == null) {
                PersistentToken token = getPersistentToken(cookieTokens);
//                User user = userService.findUserByLogin(token.getUserId());
                login = token.getUserLogin();

                // Token also matches, so login is valid. Update the token value, keeping the *same* series number.
                log.debug("Refreshing persistent login token for user '{}', series '{}'", login, token.getSeries());
                token.setTokenDate(LocalDate.now());
                token.setTokenValue(RandomUtil.generateTokenData());
                token.setIpAddress(request.getRemoteAddr());
                token.setUserAgent(request.getHeader("User-Agent"));

//                persistentTokenMapper.updateByPrimaryKey(token);
                persistentTokenService.update(token);
                try {

                } catch (DataAccessException e) {
                    log.error("Failed to update token: ", e);
                    throw new RememberMeAuthenticationException("Autologin failed due to data access problem", e);
                }
                addCookie(token, request, response);
                upgradedTokenCache.put(cookieTokens[0], new UpgradedRememberMeToken(cookieTokens, login));
            }
            return getUserDetailsService().loadUserByUsername(login);
        }
    }

    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication
            successfulAuthentication) {

        String login = successfulAuthentication.getName();

        log.debug("Creating new persistent login for user {}", login);
        PersistentToken token = userService.findUserByLogin(login).map(u -> {
            PersistentToken t = new PersistentToken();
            t.setSeries(RandomUtil.generateSeriesData());
            t.setUserLogin(u.getLogin());
            t.setTokenValue(RandomUtil.generateTokenData());
            t.setTokenDate(LocalDate.now());
            t.setIpAddress(request.getRemoteAddr());
            t.setUserAgent(request.getHeader("User-Agent"));
            return t;
        }).orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found in the database"));
        try {
            persistentTokenService.saveOrUpdate(token);
            addCookie(token, request, response);
        } catch (DataAccessException e) {
            log.error("Failed to save persistent token ", e);
        }
    }

    /**
     * When logout occurs, only invalidate the current token, and not all user sessions.
     * <p>
     * The standard Spring Security implementations are too basic: they invalidate all tokens for the
     * current user, so when he logs out from one browser, all his other sessions are destroyed.
     */
    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String rememberMeCookie = extractRememberMeCookie(request);
        if (rememberMeCookie != null && rememberMeCookie.length() != 0) {
            try {
                String[] cookieTokens = decodeCookie(rememberMeCookie);
                PersistentToken token = getPersistentToken(cookieTokens);
//                persistentTokenMapper.delete(token);
                persistentTokenService.delete(token);
            } catch (InvalidCookieException ice) {
                log.info("Invalid cookie, no persistent token could be deleted", ice);
            } catch (RememberMeAuthenticationException rmae) {
                log.debug("No persistent token found, so no token could be deleted", rmae);
            }
        }
        super.logout(request, response, authentication);
    }

    /**
     * Validate the token and return it.
     */
    private PersistentToken getPersistentToken(String[] cookieTokens) {
        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 +
                    " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }
        String presentedSeries = cookieTokens[0];
        String presentedToken = cookieTokens[1];
        Optional<PersistentToken> token = persistentTokenService.findOneBySeries(presentedSeries);

        if (!token.isPresent()) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }

        // We have a match for this user/series combination
        log.info("presentedToken={} / tokenValue={}", presentedToken, token.get().getTokenValue());
        if (!presentedToken.equals(token.get().getTokenValue())) {
            // Token doesn't match series value. Delete this session and throw an exception.
//            persistentTokenMapper.delete(token.get());
            persistentTokenService.delete(token.get());
            throw new CookieTheftException("Invalid remember-me token (Series/token) mismatch. Implies previous " +
                    "cookie theft attack.");
        }

        if (token.get().getTokenDate().plusDays(TOKEN_VALIDITY_DAYS).isBefore(LocalDate.now())) {
//            persistentTokenMapper.delete(token.get());
            persistentTokenService.delete(token.get());
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }
        return token.get();
    }

    private void addCookie(PersistentToken token, HttpServletRequest request, HttpServletResponse response) {
        setCookie(
                new String[]{token.getSeries(), token.getTokenValue()},
                TOKEN_VALIDITY_SECONDS, request, response);
    }

    private static class UpgradedRememberMeToken implements Serializable {

        private static final long serialVersionUID = 1L;

        private String[] upgradedToken;

        private Date upgradeTime;

        private String userLogin;

        UpgradedRememberMeToken(String[] upgradedToken, String userLogin) {
            this.upgradedToken = upgradedToken;
            this.userLogin = userLogin;
            this.upgradeTime = new Date();
        }

        String getUserLoginIfValidAndRecentUpgrade(String[] currentToken) {
            if (currentToken[0].equals(this.upgradedToken[0]) &&
                    currentToken[1].equals(this.upgradedToken[1]) &&
                    (upgradeTime.getTime() + UPGRADED_TOKEN_VALIDITY_SECONDS * 1000) > new Date().getTime()) {
                return this.userLogin;
            }
            return null;
        }
    }
}
