package io.buji.pac4j.profile;

import io.buji.pac4j.util.ShiroHelper;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

/**
 * Specific profile manager for Shiro.
 *
 * @author Jerome Leleu
 * @since 2.0.2
 */
public class ShiroProfileManager extends ProfileManager<CommonProfile> {

    public ShiroProfileManager(final WebContext context) {
        super(context);
    }

    @Override
    public void save(final boolean saveInSession, final CommonProfile profile, final boolean multiProfile) {
        super.save(saveInSession, profile, multiProfile);

        ShiroHelper.populateSubject(retrieveAll(saveInSession));
    }
}
