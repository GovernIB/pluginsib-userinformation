package org.fundaciobit.pluginsib.userinformation.ldap;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

import org.fundaciobit.pluginsib.userinformation.AbstractUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.SearchStatus;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.core.v3.utils.CertificateUtils;
import org.fundaciobit.pluginsib.utils.ldap.LDAPConstants;
import org.fundaciobit.pluginsib.utils.ldap.LDAPUser;
import org.fundaciobit.pluginsib.utils.ldap.LDAPUserManager;
import org.fundaciobit.pluginsib.utils.templateengine.TemplateEngine;

/**
 * Implementació del plugin de informació d'usuari amb LDAP.
 * 
 * @author anadal
 * @author areus
 */
public class LdapUserInformationPlugin extends AbstractUserInformationPlugin {

    public static final String LDAP_BASE_PROPERTIES = USERINFORMATION_BASE_PROPERTY + "ldap.";

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
            for (String attrib : LDAPConstants.LDAP_PROPERTIES) {
                String value = getProperty(USERINFORMATION_BASE_PROPERTY + attrib);
                if (value == null) {
                    log.debug("Property[" + USERINFORMATION_BASE_PROPERTY + attrib + " is NULL");
                } else {
                    ldapProperties.setProperty(attrib, value);
                }
            }

            ldapUserManager = new LDAPUserManager(ldapProperties);
        }
        return ldapUserManager;
    }

    @Override
    public boolean isImplementedRolesQueries() {
        return true;
    }

    @Override
    public RolesInfo getRolesByUsername(String username) throws Exception {

        LDAPUserManager ldapManager = getLDAPUserManager();
        List<String> roles = ldapManager.getRolesOfUser(username);

        if (roles == null) {
            return null;
        } else {
            return new RolesInfo(username, roles.toArray(new String[roles.size()]));
        }
    }

    @Override
    public boolean isImplementedUserInfoByAdministrationID() {
        return true;
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

        UserInfo info = ldapUserToUserInfo(ldapUser);
        return info;
    }

    private UserInfo ldapUserToUserInfo(LDAPUser ldapUser) throws IOException {
        if (ldapUser == null) {
            return null;
        }

        UserInfo info = new UserInfo();

        info.setName(ldapUser.getName());
        if (ldapUser.getSurname1() == null) {
            info.setSurname1(ldapUser.getSurnames());
        } else {
            info.setSurname1(ldapUser.getSurname1());
        }
        info.setSurname2(ldapUser.getSurname2());

        info.setAdministrationID(ldapUser.getAdministrationID());
        info.setUsername(ldapUser.getUserName());
        info.setEmail(ldapUser.getEmail());

        info.setCompanyDepartment(ldapUser.getDepartment());

        info.setPhoneNumber(ldapUser.getTelephoneNumber());

        // https://github.com/GovernIB/pluginsib-userinformation/issues/16

        // (1) Default language
        {
            String defLang = getProperty(LDAP_BASE_PROPERTIES + "defaultlanguage");
            if (defLang == null || defLang.trim().length() == 0) {
                defLang = "ca";
            }
            info.setLanguage(defLang);
        }

        // (2) Mail Expression Language
        {
            String mailEL = getProperty(LDAP_BASE_PROPERTIES + "mailEL");
            if (mailEL != null && mailEL.trim().length() != 0) {
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("userInfo", info);
                info.setEmail(TemplateEngine.processExpressionLanguage(mailEL, parameters));
            }
        }
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

        List<LDAPUser> users = ldapManager.getUsersByRol(rol);
        List<String> usernames = new ArrayList<String>();
        for (LDAPUser u : users) {
            usernames.add(u.getUserName());
        }

        return usernames.toArray(new String[usernames.size()]);

        /*
        List<String> allUsernames = ldapManager.getAllUserNames();
        
        List<String> usernames = new ArrayList<String>();
        for (String un : allUsernames) {
            List<String> roles = ldapManager.getRolesOfUser(un);
            if (roles.contains(rol)) {
                usernames.add(un);
            }
        }
        return usernames.toArray(new String[0]);
        */
    }

    @Override
    public UserInfo[] getUserInfoByRol(String rol) throws Exception {
        LDAPUserManager ldapManager = getLDAPUserManager();
        List<LDAPUser> users = ldapManager.getUsersByRol(rol);
        UserInfo[] userInfos = new UserInfo[users.size()];
        int count = 0;
        for (LDAPUser u : users) {
            userInfos[count] = this.ldapUserToUserInfo(u);
            count++;
        }
        return userInfos;
    }
    
    
    // TODO
    // TODO @override
    public boolean isImplementedUsersByDepartment() {
        return true;
    }
    
    // TODO @override
    public SearchUsersResult getUsersByDepartment(String department) throws Exception {

        Map<String, String> partialValuesByAttributeKey = new HashMap<String, String>();
    
        partialValuesByAttributeKey.put(LDAPUserManager.LDAP_DEPARTMENT_ATTRIBUTE, department);
        final boolean operationOr = true;
        
        return getUsersByPartialMultipleValues(partialValuesByAttributeKey, operationOr);
    }

    @Override
    public long countAllUsers() throws Exception {
        LDAPUserManager ldapManager = getLDAPUserManager();
        NamingEnumeration<SearchResult> results = ldapManager.searchLDAP(null, new String[0]);

        long count = 0;
        while (results.hasMore()) {
            results.next();
            count++;
        }

        return count;

    }

    @Override
    public SearchUsersResult getUsersByPartialNameOrPartialSurnames(String partialNameOrSurname) throws Exception {
        final String partialValue = partialNameOrSurname;

        LDAPUserManager ldapManager = getLDAPUserManager();

        String surname1Key = ldapManager.getLdapProperty(LDAPUserManager.LDAP_SURNAME1_ATTRIBUTE);
        String surname2Key = ldapManager.getLdapProperty(LDAPUserManager.LDAP_SURNAME2_ATTRIBUTE);

        final String[] attributeKeys;
        if (surname1Key == null && surname2Key == null) {

            attributeKeys = new String[] { LDAPUserManager.LDAP_NAME_ATTRIBUTE,
                    LDAPUserManager.LDAP_SURNAMES_ATTRIBUTE };
        } else {
            attributeKeys = new String[] { LDAPUserManager.LDAP_NAME_ATTRIBUTE, LDAPUserManager.LDAP_SURNAME1_ATTRIBUTE,
                    LDAPUserManager.LDAP_SURNAME2_ATTRIBUTE };
        }
        final boolean operationOR = true;
        return internalPartialSearch(partialValue, attributeKeys, operationOR);
    }

    @Override
    public SearchUsersResult getUsersByPartialEmail(String partialEmail) throws Exception {
        final String partialValue = partialEmail;
        final String attributeKey = LDAPUserManager.LDAP_EMAIL_ATTRIBUTE;

        return internalPartialSearch(partialValue, attributeKey);
    }

    @Override
    public SearchUsersResult getUsersByPartialUserName(String partialUsername) throws Exception {
        String partialValue = partialUsername;
        String attributeKey = LDAPUserManager.LDAP_USERNAME_ATTRIBUTE;
        return internalPartialSearch(partialValue, attributeKey);
    }

    @Override
    public SearchUsersResult getUsersByPartialAdministrationID(String partialAdministratorID) throws Exception {

        String partialValue = partialAdministratorID;
        String attributeKey = LDAPUserManager.LDAP_ADMINISTRATIONID_ATTRIBUTE;

        return internalPartialSearch(partialValue, attributeKey);
    }
    
    
    


    private SearchUsersResult internalPartialSearch(String partialValue, String attributeKey)
            throws NamingException, IOException, Exception {
        return internalPartialSearch(partialValue, new String[] { attributeKey }, false);
    }


    private SearchUsersResult internalPartialSearch(String partialValue, String[] attributeKeys, boolean operationOr)
            throws NamingException, IOException, Exception {
        Map<String, String> partialValuesByAttributeKey = new HashMap<String, String>();
        for (String attributeKey : attributeKeys) {
            partialValuesByAttributeKey.put(attributeKey, partialValue);
        }
        return getUsersByPartialMultipleValues(partialValuesByAttributeKey, operationOr);
    }

    @Override
    public boolean isImplementedUsersByPartialValuesAnd() {
        return true;
    }

    @Override
    public SearchUsersResult getUsersByPartialValuesAnd(String usernamePartial, String firstNamePartial,
            String lastNamePartial, String emailPartial, String administrationIDPartial) throws Exception {
        final boolean operationOr = false;
        return getUsersByPartialValuesAndOr(usernamePartial, firstNamePartial, lastNamePartial, emailPartial,
                administrationIDPartial, operationOr);
    }
    
    @Override
    public boolean isImplementedUsersByPartialValuesOr() {
        return true;
    }

    @Override
    public SearchUsersResult getUsersByPartialValuesOr(String usernamePartial, String firstNamePartial,
            String lastNamePartial, String emailPartial, String administrationIDPartial) throws Exception {
        final boolean operationOr = true;

        return getUsersByPartialValuesAndOr(usernamePartial, firstNamePartial, lastNamePartial, emailPartial,
                administrationIDPartial, operationOr);
    }

    private SearchUsersResult getUsersByPartialValuesAndOr(String usernamePartial, String firstNamePartial,
            String lastNamePartial, String emailPartial, String administrationIDPartial, final boolean operationOr)
            throws Exception {
        Map<String, String> partialValuesByAttributeKey = new HashMap<String, String>();

        if (usernamePartial != null && usernamePartial.trim().length() > 0) {
            partialValuesByAttributeKey.put(LDAPUserManager.LDAP_USERNAME_ATTRIBUTE, usernamePartial);
        }

        if (firstNamePartial != null && firstNamePartial.trim().length() > 0) {
            partialValuesByAttributeKey.put(LDAPUserManager.LDAP_NAME_ATTRIBUTE, firstNamePartial);
        }

        LDAPUserManager ldapManager = getLDAPUserManager();
        String surname1Key = ldapManager.getLdapProperty(LDAPUserManager.LDAP_SURNAME1_ATTRIBUTE);
        String surname2Key = ldapManager.getLdapProperty(LDAPUserManager.LDAP_SURNAME2_ATTRIBUTE);

        if (surname1Key == null && surname2Key == null) {

            partialValuesByAttributeKey.put(LDAPUserManager.LDAP_SURNAMES_ATTRIBUTE, lastNamePartial);
        } else {

            partialValuesByAttributeKey.put(LDAPUserManager.LDAP_SURNAME1_ATTRIBUTE, lastNamePartial);
            if (operationOr) {
                partialValuesByAttributeKey.put(LDAPUserManager.LDAP_SURNAME2_ATTRIBUTE, lastNamePartial);
            }
        }

        if (emailPartial != null && emailPartial.trim().length() > 0) {
            partialValuesByAttributeKey.put(LDAPUserManager.LDAP_EMAIL_ATTRIBUTE, emailPartial);
        }

        if (administrationIDPartial != null && administrationIDPartial.trim().length() > 0) {
            partialValuesByAttributeKey.put(LDAPUserManager.LDAP_ADMINISTRATIONID_ATTRIBUTE, administrationIDPartial);
        }

        return getUsersByPartialMultipleValues(partialValuesByAttributeKey, operationOr);
    }



    /**
     * 
     * @param partialValuesByAttributeKey Key => Key Values of LDAPConstants. Values => Partial pattern to search
     * @param operationOr
     * @return
     * @throws Exception
     */
    private SearchUsersResult getUsersByPartialMultipleValues(Map<String, String> partialValuesByAttributeKey,
            boolean operationOr) throws Exception {

        LDAPUserManager ldapManager = getLDAPUserManager();

        if (partialValuesByAttributeKey == null || partialValuesByAttributeKey.size() == 0) {
            return new SearchUsersResult(new SearchStatus(SearchStatus.RESULT_PARTIAL_STRING_NULL_OR_EMPTY));
        }

        StringBuilder filterB = new StringBuilder();
        for (Entry<String, String> entry : partialValuesByAttributeKey.entrySet()) {

            String attributeKey = entry.getKey();
            String partialValue = entry.getValue();
            String ldapAttrib = ldapManager.getLdapProperties().getProperty(attributeKey);
            if (ldapAttrib == null || ldapAttrib.trim().length() == 0) {
                return new SearchUsersResult(new SearchStatus(SearchStatus.RESULT_CLIENT_ERROR,
                        "L´atribut " + attributeKey + " no està definit en els propietats"));
            }
            filterB.append("(").append(ldapAttrib).append("=").append(partialValue).append(")");
        }
        try {
            String filter;
            if (partialValuesByAttributeKey.size() == 1) {
                filter = filterB.toString();
            } else {
                if (operationOr) {
                    filter = "(|" + filterB.toString() + ")";
                } else {
                    filter = "(&" + filterB.toString() + ")";
                }
            }
            NamingEnumeration<SearchResult> enumeration = ldapManager.searchLDAP(filter, null);

            return ldapNamingEnumeration2UserInfoList(ldapManager, enumeration);
        } catch (javax.naming.SizeLimitExceededException e) {
            return new SearchUsersResult(new SearchStatus(SearchStatus.RESULT_TOO_MANY_RESULTS_MATCH, e.getMessage()));
        }
    }

    private SearchUsersResult ldapNamingEnumeration2UserInfoList(LDAPUserManager ldapManager, NamingEnumeration<SearchResult> enumeration)
            throws NamingException, IOException, Exception {
        
        List<UserInfo> list = new ArrayList<UserInfo>();
        while (enumeration.hasMore()) {
            SearchResult sr = enumeration.next();
            list.add(ldapUserToUserInfo(ldapManager.convertAttributesToLdapUser(sr.getAttributes())));
        }
        return new SearchUsersResult(list);
    }

    @Override
    public boolean isImplementedAuthenticationByUsernamePasword() {
        return true;
    }

    @Override
    public boolean isImplementedAuthenticationByCertificate() {
        return false;
    }

    @Override
    protected int getMinimumCharactersToSearch() {
        return 0;
    }

}
