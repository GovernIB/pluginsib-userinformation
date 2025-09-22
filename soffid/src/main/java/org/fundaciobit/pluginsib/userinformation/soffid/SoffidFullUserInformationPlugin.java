package org.fundaciobit.pluginsib.userinformation.soffid;

import java.util.ArrayList;
import java.util.List;

import org.fundaciobit.pluginsib.userinformation.soffid.beans.Resource;

/**
 * 
 * @author anadal
 * 25 ago 2025 12:54:56
 */
public class SoffidFullUserInformationPlugin extends SoffidUserInformationPlugin {

    @Override
    public String[] getAllUsernames() throws Exception {

        String urlOperationBase = "/User?sortBy=lastName&sortOrder=ascending";

        List<Resource> results = consultaPaginada(urlOperationBase, isDebug(), false);

        List<String> usernames = new ArrayList<String>(results.size());

        for (Resource resource : results) {
            usernames.add(resource.getUserName());
        }

        return usernames.toArray(new String[usernames.size()]);

    }
}
