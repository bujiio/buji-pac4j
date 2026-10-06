package io.buji.pac4j.token;

import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the {@link Pac4jToken}.
 *
 * @author Jerome Leleu
 * @since 10.0.1
 */
public final class Pac4jTokenTests {

    @Test
    public void testPrincipalIsTheProfile() {
        final CommonProfile profile = new CommonProfile();
        profile.setId("id");

        final Pac4jToken token = new Pac4jToken(List.of(profile), false);

        assertSame(profile, token.getPrincipal());
    }

    @Test
    public void testNoProfile() {
        assertNull(new Pac4jToken(new ArrayList<>(), false).getPrincipal());
    }
}
