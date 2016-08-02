package io.buji.pac4j.subject;

import org.apache.shiro.io.DefaultSerializer;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;

import java.util.LinkedHashMap;

import static org.junit.Assert.*;

/**
 * Tests {@link Pac4jPrincipal}.
 *
 * @author Jerome Leleu
 * @since 2.0.1
 */
public final class Pac4jPrincipalTests {

    @Test
    public void testSerialize() {
        final LinkedHashMap<String, CommonProfile> profiles = new LinkedHashMap<>();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles);

        final DefaultSerializer serializer = new DefaultSerializer();
        final byte[] serialized = serializer.serialize(principal);
        final Pac4jPrincipal principal2 = (Pac4jPrincipal) serializer.deserialize(serialized);
        assertEquals(principal2, principal);
    }
}
