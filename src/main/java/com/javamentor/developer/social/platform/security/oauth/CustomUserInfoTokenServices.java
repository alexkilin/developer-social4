package com.javamentor.developer.social.platform.security.oauth;

import com.javamentor.developer.social.platform.service.abstracts.model.user.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedAuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedPrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CustomUserInfoTokenServices implements ResourceServerTokenServices {

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    private OauthUserInfoExtractorService externalUserExtractorService;

    protected final Log logger = LogFactory.getLog(getClass());

    private final String userInfoEndpointUrl;

    private final String clientId;

    private OAuth2RestOperations restTemplate;

    private String tokenType = DefaultOAuth2AccessToken.BEARER_TYPE;

    private AuthoritiesExtractor authoritiesExtractor = new FixedAuthoritiesExtractor();

    private PrincipalExtractor principalExtractor = new FixedPrincipalExtractor();

    public CustomUserInfoTokenServices( String userInfoEndpointUrl , String clientId ) {
        this.userInfoEndpointUrl = userInfoEndpointUrl;
        this.clientId = clientId;
    }

    public void setExternalUserExtractorService( OauthUserInfoExtractorService externalUserExtractorService ) {
        this.externalUserExtractorService = externalUserExtractorService;
    }

    public void setUserService( UserService userService ) {
        this.userService = userService;
    }

    public void setPasswordEncoder( PasswordEncoder passwordEncoder ) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setTokenType( String tokenType ) {
        this.tokenType = tokenType;
    }

    public void setRestTemplate( OAuth2RestOperations restTemplate ) {
        this.restTemplate = restTemplate;
    }

    public void setAuthoritiesExtractor( AuthoritiesExtractor authoritiesExtractor ) {
        Assert.notNull(authoritiesExtractor , "AuthoritiesExtractor must not be null");
        this.authoritiesExtractor = authoritiesExtractor;
    }

    public void setPrincipalExtractor( PrincipalExtractor principalExtractor ) {
        Assert.notNull(principalExtractor , "PrincipalExtractor must not be null");
        this.principalExtractor = principalExtractor;
    }

    @Override
    public OAuth2Authentication loadAuthentication( String accessToken )
            throws AuthenticationException, InvalidTokenException {
        Map<String, Object> map = getMap(this.userInfoEndpointUrl , accessToken);

        if(map.containsKey("error")) {
            if(this.logger.isDebugEnabled()) {
                this.logger.debug("userinfo returned error: " + map.get("error"));
            }
            throw new InvalidTokenException(accessToken);
        }

        externalUserExtractorService.getUser(map);
        return extractAuthentication(map);
    }

    private OAuth2Authentication extractAuthentication( Map<String, Object> map ) {
        Object principal = getPrincipal(map);
        List<GrantedAuthority> authorities = this.authoritiesExtractor
                .extractAuthorities(map);
        OAuth2Request request = new OAuth2Request(null , this.clientId , null , true , null ,
                null , null , null , null);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                principal , "N/A" , authorities);
        token.setDetails(map);
        return new OAuth2Authentication(request , token);
    }

    protected Object getPrincipal( Map<String, Object> map ) {
        Object principal = this.principalExtractor.extractPrincipal(map);
        return ( principal == null ? "unknown" : principal );
    }

    @Override
    public OAuth2AccessToken readAccessToken( String accessToken ) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }

    @SuppressWarnings({"unchecked"})
    private Map<String, Object> getMap( String path , String accessToken ) {
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("Getting user info from: " + path);
        }
        try {
            OAuth2RestOperations restTemplate = this.restTemplate;
            if(restTemplate == null) {
                BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
                resource.setClientId(this.clientId);
                restTemplate = new OAuth2RestTemplate(resource);
            }
            OAuth2AccessToken existingToken = restTemplate.getOAuth2ClientContext()
                    .getAccessToken();
            if(existingToken == null || !accessToken.equals(existingToken.getValue())) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(
                        accessToken);
                token.setTokenType(this.tokenType);
                restTemplate.getOAuth2ClientContext().setAccessToken(token);
            }
            return restTemplate.getForEntity(path , Map.class).getBody();
        } catch (Exception ex) {
            this.logger.warn("Could not fetch user details: " + ex.getClass() + ", "
                    + ex.getMessage());
            return Collections.<String, Object>singletonMap("error" ,
                    "Could not fetch user details");
        }
    }
}
