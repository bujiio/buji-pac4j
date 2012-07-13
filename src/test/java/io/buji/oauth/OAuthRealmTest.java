package io.buji.oauth;

import io.buji.oauth.OAuthAuthenticationException;
import io.buji.oauth.OAuthRealm;
import io.buji.oauth.OAuthToken;

import java.util.Map;

import junit.framework.TestCase;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.UserProfile;

/**
 * This class tests the OAuthRealm.
 * 
 * @author Jerome Leleu
 */
public final class OAuthRealmTest extends TestCase {
    
    private static final String ROLE1 = "ROLE1";
    
    private static final String ROLE2 = "ROLE2";
    
    private static final String PERM1 = "PERM1";
    
    private static final String PERM2 = "PERM2";
    
    private static final String PERM3 = "PERM3";
    
    private OAuthRealm createOAuthRealm() {
        OAuthRealm oauthRealm = new OAuthRealm();
        oauthRealm.setProvider(new MockOAuthProvider());
        return oauthRealm;
    }
    
    public void testNullToken() {
        AuthenticationInfo authenticationInfo = createOAuthRealm().doGetAuthenticationInfo(null);
        assertEquals(null, authenticationInfo);
    }
    
    public void testNullCredential() {
        AuthenticationInfo authenticationInfo = createOAuthRealm().doGetAuthenticationInfo(new OAuthToken(null));
        assertEquals(null, authenticationInfo);
    }
    
    public void testWrongProviderType() {
        AuthenticationInfo authenticationInfo = createOAuthRealm()
            .doGetAuthenticationInfo(new OAuthToken(
                                                    new OAuthCredential(
                                                                        null,
                                                                        MockOAuthProvider.USER_PROFILE_WITHOUT_ATTRIBUTE,
                                                                        null, "not" + MockOAuthProvider.TYPE)));
        assertEquals(null, authenticationInfo);
    }
    
    public void testNullUserProfileException() {
        try {
            createOAuthRealm()
                .doGetAuthenticationInfo(new OAuthToken(new OAuthCredential(null, MockOAuthProvider.NULL_USER_PROFILE,
                                                                            null, MockOAuthProvider.TYPE)));
            fail();
        } catch (Exception e) {
            assertEquals(true, e instanceof OAuthAuthenticationException);
            assertEquals(true, e.getMessage().indexOf("Unable to get user profile") >= 0);
        }
    }
    
    public void testUserProfileWithEmptyIdException() {
        try {
            createOAuthRealm()
                .doGetAuthenticationInfo(new OAuthToken(
                                                        new OAuthCredential(
                                                                            null,
                                                                            MockOAuthProvider.USER_PROFILE_WITH_EMPTY_ID,
                                                                            "", MockOAuthProvider.TYPE)));
            fail();
        } catch (Exception e) {
            assertEquals(true, e instanceof OAuthAuthenticationException);
            assertEquals(true, e.getMessage().indexOf("Unable to get user profile") >= 0);
        }
    }
    
    public void testUserProfileWithoutAttribute() {
        OAuthToken oauthToken = new OAuthToken(new OAuthCredential(null,
                                                                   MockOAuthProvider.USER_PROFILE_WITHOUT_ATTRIBUTE,
                                                                   null, MockOAuthProvider.TYPE));
        AuthenticationInfo authenticationInfo = createOAuthRealm().doGetAuthenticationInfo(oauthToken);
        UserProfile profile = (UserProfile) authenticationInfo.getPrincipals().asList().get(1);
        assertEquals(profile.getTypedId(), (String) oauthToken.getPrincipal());
        assertEquals(profile.getTypedId(), (String) authenticationInfo.getPrincipals().getPrimaryPrincipal());
        Map<String, Object> attributes = (Map<String, Object>) profile.getAttributes();
        assertEquals(0, attributes.size());
    }
    
    public void testUserProfileWithOneAttribute() {
        OAuthToken oauthToken = new OAuthToken(new OAuthCredential(null,
                                                                   MockOAuthProvider.USER_PROFILE_WITH_ONE_ATTRIBUTE,
                                                                   null, MockOAuthProvider.TYPE));
        AuthenticationInfo authenticationInfo = createOAuthRealm().doGetAuthenticationInfo(oauthToken);
        UserProfile profile = (UserProfile) authenticationInfo.getPrincipals().asList().get(1);
        assertEquals(profile.getTypedId(), (String) oauthToken.getPrincipal());
        assertEquals(profile.getTypedId(), (String) authenticationInfo.getPrincipals().getPrimaryPrincipal());
        Map<String, Object> attributes = (Map<String, Object>) profile.getAttributes();
        assertEquals(1, attributes.size());
        assertEquals(MockOAuthProvider.ATTRIBUTE_VALUE, attributes.get(MockOAuthProvider.ATTRIBUTE_KEY));
    }
    
    public void testSimpleRolePermission() {
        OAuthRealm oauthRealm = createOAuthRealm();
        OAuthToken oauthToken = new OAuthToken(new OAuthCredential(null,
                                                                   MockOAuthProvider.USER_PROFILE_WITHOUT_ATTRIBUTE,
                                                                   null, MockOAuthProvider.TYPE));
        AuthenticationInfo authenticationInfo = oauthRealm.doGetAuthenticationInfo(oauthToken);
        oauthRealm.setDefaultRoles(ROLE1);
        oauthRealm.setDefaultPermissions(PERM1);
        AuthorizationInfo authorizationInfo = oauthRealm.doGetAuthorizationInfo(authenticationInfo.getPrincipals());
        assertEquals(ROLE1, authorizationInfo.getRoles().iterator().next());
        assertEquals(PERM1, authorizationInfo.getStringPermissions().iterator().next());
    }
    
    public void testMultipleRolePermission() {
        OAuthRealm oauthRealm = createOAuthRealm();
        OAuthToken oauthToken = new OAuthToken(new OAuthCredential(null,
                                                                   MockOAuthProvider.USER_PROFILE_WITHOUT_ATTRIBUTE,
                                                                   null, MockOAuthProvider.TYPE));
        AuthenticationInfo authenticationInfo = oauthRealm.doGetAuthenticationInfo(oauthToken);
        oauthRealm.setDefaultRoles(ROLE1 + "," + ROLE2);
        oauthRealm.setDefaultPermissions(PERM1 + "," + PERM2 + "," + PERM3);
        AuthorizationInfo authorizationInfo = oauthRealm.doGetAuthorizationInfo(authenticationInfo.getPrincipals());
        assertEquals(true, authorizationInfo.getRoles().contains(ROLE1));
        assertEquals(true, authorizationInfo.getRoles().contains(ROLE2));
        assertEquals(2, authorizationInfo.getRoles().size());
        assertEquals(true, authorizationInfo.getStringPermissions().contains(PERM1));
        assertEquals(true, authorizationInfo.getStringPermissions().contains(PERM2));
        assertEquals(true, authorizationInfo.getStringPermissions().contains(PERM3));
        assertEquals(3, authorizationInfo.getStringPermissions().size());
    }
}
