package org.fundaciobit.pluginsib.userinformation.database;

import java.util.Properties;

/**
 * Per mantenir la retrocompatibilitat 
 * 
 * @author anadal
 *
 */
public class DataBaseUserInformationPlugin extends org.fundaciobit.plugins.userinformation.database.DataBaseUserInformationPlugin {

    public DataBaseUserInformationPlugin() {
        super();
    }

    public DataBaseUserInformationPlugin(String propertyKeyBase, Properties properties) {
        super(propertyKeyBase, properties);
    }

    public DataBaseUserInformationPlugin(String propertyKeyBase) {
        super(propertyKeyBase);
    }

}
