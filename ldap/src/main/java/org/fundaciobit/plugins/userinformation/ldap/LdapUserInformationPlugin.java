package org.fundaciobit.plugins.userinformation.ldap;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.fundaciobit.pluginsib.userinformation.IUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.core.utils.AbstractPluginProperties;
import org.fundaciobit.pluginsib.core.utils.CertificateUtils;
import org.fundaciobit.pluginsib.utils.ldap.LDAPConstants;
import org.fundaciobit.pluginsib.utils.ldap.LDAPUser;
import org.fundaciobit.pluginsib.utils.ldap.LDAPUserManager;

/**
 * Implementació del plugin de informació d'usuari amb LDAP.
 * @author anadal
 * @author areus
 */
public class LdapUserInformationPlugin extends AbstractPluginProperties
   implements IUserInformationPlugin {

  public static final String LDAP_BASE_PROPERTIES = USERINFORMATION_BASE_PROPERTY;

  private LDAPUserManager ldapUserManager = null;

  public LdapUserInformationPlugin() {
    super();
  }

  public LdapUserInformationPlugin(String propertyKeyBase, Properties properties) {
    super(propertyKeyBase, properties);
  }

  public LdapUserInformationPlugin(String propertyKeyBase) {
    super(propertyKeyBase);
  }

  public LDAPUserManager getLDAPUserManager() {

    if (ldapUserManager == null) {

      Properties ldapProperties = new Properties();
      for (String attrib :  LDAPConstants.LDAP_PROPERTIES) {
	    String value = getProperty(LDAP_BASE_PROPERTIES + attrib);
		if (value == null) {
		    System.err.println("Property[" + LDAP_BASE_PROPERTIES + attrib + " is NULL");
		} else {
          ldapProperties.setProperty(attrib, value);
		}
      }

      ldapUserManager = new LDAPUserManager(ldapProperties);
    }
    return ldapUserManager;
  }

  @Override
  public RolesInfo getRolesByUsername(String username) throws Exception {

    LDAPUserManager ldapManager = getLDAPUserManager();
    List<String> roles = ldapManager.getRolesOfUser(username);
    
    if (roles == null) {
      return null;
    } else {
      return new RolesInfo(username, roles.toArray(new String[0]));
    }
  }

  public UserInfo getUserInfoByAdministrationID(String nif) throws Exception {
    final boolean paramIsNif = true;
    return getUserInfo(paramIsNif, nif);
  }

  public UserInfo getUserInfoByUserName(String username) throws Exception {
    final boolean paramIsNif = false;
    return getUserInfo(paramIsNif, username);
  }
  
  private UserInfo getUserInfo(boolean paramIsNif, String param) throws Exception {
    LDAPUserManager ldapManager = getLDAPUserManager();
    LDAPUser ldapUser;
    if (paramIsNif) {
      ldapUser = ldapManager.getUserByAdministrationID(param);
    } else {
      ldapUser = ldapManager.getUserByUsername(param);
    }
    
    if (ldapUser == null) {
      return null;
    }
    
    UserInfo info = new UserInfo();
    info.setLanguage("ca");
    info.setName(ldapUser.getName());
    if(ldapUser.getSurname1() == null) {
      info.setSurname1(ldapUser.getSurnames());
    } else {
      info.setSurname1(ldapUser.getSurname1());
    }
    info.setSurname2(ldapUser.getSurname2());

    info.setAdministrationID(ldapUser.getAdministrationID());
    info.setUsername(ldapUser.getUserName());
    info.setEmail(ldapUser.getEmail());
    info.setPhoneNumber(ldapUser.getTelephoneNumber());
    
    return info;
  }


  @Override
  public boolean authenticate(String username, String password) {
    LDAPUserManager ldapManager = getLDAPUserManager();
    return ldapManager.authenticateUser(username, password);

  }

  @Override
  public boolean authenticate(X509Certificate certificate) throws Exception {
    if (certificate == null) {
      return false;
    }
    
    String nif = CertificateUtils.getDNI(certificate);
    if (nif == null) {
      throw new Exception("No puc extreure el NIF del Certificat " + certificate.toString());
    }

    return getUserInfoByAdministrationID(nif) != null;
  }

  @Override
  public String[] getAllUsernames() throws Exception {
    LDAPUserManager ldapManager = getLDAPUserManager();
    List<String> usernames = ldapManager.getAllUserNames();
    return usernames.toArray(new String[0]);
  }

  @Override
  public String[] getUsernamesByRol(String rol) throws Exception {
    LDAPUserManager ldapManager = getLDAPUserManager();
    List<String> allUsernames = ldapManager.getAllUserNames();
    
    List<String> usernames = new ArrayList<String>();
    for (String un : allUsernames) {
      List<String> roles = ldapManager.getRolesOfUser(un);
      if (roles.contains(rol)) {
        usernames.add(un);
      }
    }
    return usernames.toArray(new String[0]);
  }

}
