package org.fundaciobit.pluginsib.userinformation.ldap;

import java.util.Properties;

/**
 * Per mantenir la retrocompatibilitat 
 *
 * @author anadal
 *
 */
public class LdapUserInformationPlugin extends org.fundaciobit.plugins.userinformation.ldap.LdapUserInformationPlugin {

    public LdapUserInformationPlugin() {
        super();
    }

    public LdapUserInformationPlugin(String propertyKeyBase, Properties properties) {
        super(propertyKeyBase, properties);
    }

    public LdapUserInformationPlugin(String propertyKeyBase) {
        super(propertyKeyBase);
    }

}
