package org.fundaciobit.pluginsib.userinformation.soffid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.fundaciobit.pluginsib.userinformation.UserInfo;

import java.util.Set;

/**
 * 
 * @author anadal
 *
 */
public class CacheNifUsername {

    /** Map de Key Nif i Value Username */
    protected Map<String, String> cacheUsernameByNif = new HashMap<String, String>();

    /** Guarda els usernames quan els nifs dels usuaris valen 0 */
    protected Set<String> usernamesWithNullNif = new HashSet<String>();

    protected Long completeNifUsernameDate = null;

    public boolean isNifCacheComplete() {

        if (completeNifUsernameDate != null) {

            // XYZ ZZZ TODO a Property
            // Permetrem 1 hora de Cache
            final long ONE_HOUR = 60 * 60 * 1000L;

            if (System.currentTimeMillis() - completeNifUsernameDate < ONE_HOUR) {
                return true;
            }
        }
        return false;
    }

    public void setNifCacheComplete() {
        completeNifUsernameDate = System.currentTimeMillis();
    }

    public String getUsernameByNif(String administrationID) {
        return cacheUsernameByNif.get(administrationID);
    }

    public Set<Entry<String, String>> entrySet() {
        return cacheUsernameByNif.entrySet();
    }

    public String getNifAttributeAndUpdateCache(final String attributeUserNIF, UserInfo ur) {
        if (ur.getAttributes() != null) {
            {
                String nif = ur.getAdministrationID();
                cacheUsernameByNif.put(nif, ur.getUsername());
                return nif;
            }
        }
        usernamesWithNullNif.add(ur.getUsername());
        return null;
    }

}
