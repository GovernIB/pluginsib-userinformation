package org.fundaciobit.pluginsib.userinformation.keycloak;

import java.lang.reflect.Field;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.fundaciobit.pluginsib.core.utils.AbstractPluginProperties;
import org.fundaciobit.pluginsib.userinformation.IUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.UserInfo.Gender;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

/**
 * 
 * @author anadal
 *
 */
public class KeyCloakUserInformationPlugin extends AbstractPluginProperties
        implements IUserInformationPlugin {

    private static final String PLUGINSIB_USERINFORMATION_BASE_PROPERTIES = IPLUGINSIB_BASE_PROPERTIES
            + "userinformation.";

    protected final Logger log = Logger.getLogger(getClass());

    private static final String KEYCLOAK_BASE_PROPERTY = PLUGINSIB_USERINFORMATION_BASE_PROPERTIES
            + "keycloak.";

    public static final String SERVER_URL_PROPERTY = KEYCLOAK_BASE_PROPERTY + "serverurl";
    public static final String REALM_PROPERTY = KEYCLOAK_BASE_PROPERTY + "realm";

    public static final String PASSWORD_SECRET_PROPERTY = KEYCLOAK_BASE_PROPERTY
            + "password_secret";
    public static final String CLIENT_ID_PROPERTY = KEYCLOAK_BASE_PROPERTY + "client_id";

    public static final String CLIENT_ID_FOR_USER_AUTHENTICATION_PROPERTY = KEYCLOAK_BASE_PROPERTY
            + "client_id_for_user_autentication";

    public static final String DEBUG_PROPERTY = KEYCLOAK_BASE_PROPERTY + "debug";

    public static final String MAPPING_PROPERTY = KEYCLOAK_BASE_PROPERTY + "mapping.";

    /**
     * 
     */
    public KeyCloakUserInformationPlugin() {
        super();
    }

    /**
     * @param propertyKeyBase
     */
    public KeyCloakUserInformationPlugin(String propertyKeyBase) {
        super(propertyKeyBase);
    }

    /**
     * @param propertyKeyBase
     * @param properties
     */
    public KeyCloakUserInformationPlugin(String propertyKeyBase, Properties properties) {
        super(propertyKeyBase, properties);
    }

    protected boolean isDebug() {
        String debug = getProperty(DEBUG_PROPERTY, "false");
        return "true".equals(debug);
    }

    protected RolesResource getKeyCloakConnectionForRoles() throws Exception {
        Keycloak keycloak = getKeyCloakConnection();

        return keycloak.realm(getPropertyRequired(REALM_PROPERTY)).roles();
    }

    protected UsersResource getKeyCloakConnectionForUsers() throws Exception {
        Keycloak keycloak = getKeyCloakConnection();
        UsersResource usersResource = keycloak.realm(getPropertyRequired(REALM_PROPERTY)).users();
        return usersResource;
    }

    public class CustomJacksonProvider extends ResteasyJackson2Provider {

    }

    private Keycloak getKeyCloakConnection() throws Exception {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(getPropertyRequired(SERVER_URL_PROPERTY))
                .realm(getPropertyRequired(REALM_PROPERTY))
                .clientId(getPropertyRequired(CLIENT_ID_PROPERTY))
                .clientSecret(getPropertyRequired(PASSWORD_SECRET_PROPERTY))
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS) // "client_credentials"
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10)
                        .register(new CustomJacksonProvider()).build())
                .build();

        keycloak.tokenManager().getAccessToken();
        return keycloak;
    }

    protected Keycloak getKeyCloakConnectionUsernamePassword(String username, String password)
            throws Exception {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(getPropertyRequired(SERVER_URL_PROPERTY))
                .realm(getPropertyRequired(REALM_PROPERTY))
                .clientId(getPropertyRequired(CLIENT_ID_FOR_USER_AUTHENTICATION_PROPERTY))
                .password(password).username(username).grantType(OAuth2Constants.PASSWORD) // "password"
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10)
                        .register(new CustomJacksonProvider()).build())
                .build();

        keycloak.tokenManager().getAccessToken();
        return keycloak;
    }

    protected Map<String, String> cacheNifUsername = new HashMap<String, String>();

    @Override
    public UserInfo getUserInfoByAdministrationID(String administrationID) throws Exception {

        if (cacheNifUsername.containsKey(administrationID)) {
            return getUserInfoByUserName(cacheNifUsername.get(administrationID));
        }

        // Cerca un usuari a partir del NIF

        UsersResource usersResource = getKeyCloakConnectionForUsers();

        log.info("XYZ ZZZ KEYCLOAK::getUserInfoByAdministrationID  ==>  " + usersResource);

        final int step = 20;

        int start = 0;
        int total = 0;

        List<UserRepresentation> users;

        UserInfo ui = null;

        final String attributeUserNIF = getProperty(MAPPING_PROPERTY + "administrationID");

        while ((users = usersResource.list(start, step)) != null) {

            if (users.size() == 0) {
                break;
            }

            System.out.println("  BUCLE   " + start + " - " + (start + step));

            start = start + step;

            if (attributeUserNIF == null || attributeUserNIF.trim().length() == 0) {
                log.error(
                        "La cerca empant Administration ID no és posible ja que no s'ha definit cap propietat mapping emprant la clau administrationID");
                return null;
            }

            for (UserRepresentation ur : users) {
                total++;
                if (ur.getAttributes() == null) {
                    continue;
                }

                List<String> values = ur.getAttributes().get(attributeUserNIF);
                if (values == null || values.size() == 0) {
                    continue;
                }
                String nif = values.get(0);
                if (nif == null) {
                    continue;
                }

                cacheNifUsername.put(nif, ur.getUsername());

                if (administrationID.equalsIgnoreCase(nif)) {
                    // OK
                    System.out.println("TROBAT USUARI !!!!!!! " + ur.getUsername());
                    ui = userRepresentationToUserInfo(ur);

                    return ui;

                }
            }
        }

        System.out.println("TOTAL PERSONES: " + total);

        return ui;

        /*
         * List<UserRepresentation> users = usersResource.search(administrationID,
         * //"id:b3f0e199-0f04-4b38-9807-7c45692a5c30", //"attribute:nif:" +
         * administrationID, 0, Integer.MAX_VALUE);
         * 
         * if (users == null || users.size() == 0) { return null; }
         * 
         * UserRepresentation user = users.get(0);
         * 
         * UserInfo ui = userRepresentationToUserInfo(user);
         * 
         * return ui;
         */
    }

    @Override
    public UserInfo getUserInfoByUserName(String username) throws Exception {

        if (username == null) {
            log.warn("getUserInfoByUserName():: Username is null");
            return null;
        }

        UsersResource usersResource = getKeyCloakConnectionForUsers();

        log.info("XYZ ZZZ KEYCLOAK::getUserInfoByUserName  ==>  " + usersResource);

        List<UserRepresentation> users = usersResource.search(username);

        if (users == null || users.size() == 0) {
            return null;
        }

        for (UserRepresentation user : users) {

            if (user.getUsername().equals(username)) {

                UserInfo ui = userRepresentationToUserInfo(user);

                return ui;
            }
        }

        return null;

    }

    protected UserInfo userRepresentationToUserInfo(UserRepresentation user) throws Exception {

        final boolean debug = isDebug();

        if (debug) {

            log.info("ui.setEmail => " + user.getEmail());
            log.info("ui.setName => " + user.getFirstName());
            log.info("ui.setSurname1 => " + user.getLastName());
            log.info("ui.setUsername => " + user.getUsername());

        }

        UserInfo ui = new UserInfo();
        ui.setAddress(null);
        ui.setCompany(null);
        ui.setEmail(user.getEmail());
        ui.setGender(null);
        ui.setLanguage(null);
        ui.setName(user.getFirstName());
        ui.setSurname1(user.getLastName());
        ui.setSurname2(null);
        ui.setPhoneNumber(null);
        ui.setUsername(user.getUsername());
        ui.setWebsite(null);

        {
            final Set<String> mappingsAvailable = new HashSet<String>(Arrays.asList("username",
                    "administrationID", "name", "surname1", "surname2", "email", "language",
                    "phoneNumber", "password", "gender", "address", "company", "website"));

            Map<String, List<String>> userAttributes = user.getAttributes();
            if (userAttributes != null) {

                if (debug) {
                    for (String key : userAttributes.keySet()) {
                        List<String> list = userAttributes.get(key);
                        log.info(" Attributes[" + key + "] => " + list.get(0));
                    }
                }

                for (String userInfoField : mappingsAvailable) {
                    String attributeUser = getProperty(MAPPING_PROPERTY + userInfoField);
                    if (attributeUser == null || attributeUser.trim().length() == 0) {
                        continue;
                    }

                    List<String> list = userAttributes.get(attributeUser);
                    String attributeUserValue = list.get(0);

                    if (debug) {
                        log.info(" Posant al camp " + userInfoField + " el valor "
                                + attributeUserValue);
                    }

                    Field field = ui.getClass().getDeclaredField(userInfoField);
                    field.setAccessible(true);

                    if ("gender".equals(userInfoField)) {
                        try {
                            int genderValue = Integer.parseInt(attributeUserValue);
                            switch (genderValue) {

                                case -1:
                                    ui.setGender(Gender.UNKNOWN);
                                break;
                                case 0:
                                    ui.setGender(Gender.FEMALE);
                                break;

                                case 1:
                                    ui.setGender(Gender.MALE);
                                break;

                                default:
                                    throw new Exception();

                            }

                        } catch (Exception e) {
                            log.error(" Error prosessnat mapping de GENDER (-1, 0 o 1): "
                                    + attributeUserValue);
                        }

                    } else {
                        field.set(ui, attributeUserValue);
                    }

                }

            }
        }
        return ui;
    }

    @Override
    public boolean authenticate(String username, String password) throws Exception {
        try {

            Keycloak keycloak = getKeyCloakConnectionUsernamePassword(username, password);

            UsersResource usersResource = keycloak.realm(getPropertyRequired(REALM_PROPERTY))
                    .users();

            // System.out.println("XYZ ZZZ AUTHENTIXCATE " + kc.realms().findAll()); //
            // serverInfo().getInfo().getComponentTypes());

            return true;
        } catch (javax.ws.rs.NotAuthorizedException exceptionNotAuth) {
            return false;
        } catch (Exception e) {
            throw e;
        } catch (Throwable th) {
            log.error(th.getMessage(), th);
        }
        return false;
    }

    @Override
    public boolean authenticate(X509Certificate certificate) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String[] getAllUsernames() throws Exception {

        UsersResource usersResource = getKeyCloakConnectionForUsers();

        List<UserRepresentation> all = usersResource.list();
        List<String> usuaris = new ArrayList<String>();

        for (UserRepresentation ur : all) {
            usuaris.add(ur.getUsername());
        }
        return usuaris.toArray(new String[usuaris.size()]);
    }

    @Override
    public RolesInfo getRolesByUsername(String username) throws Exception {

        UsersResource usersResource = getKeyCloakConnectionForUsers();

        List<UserRepresentation> users = usersResource.search(username);

        // users.get(0).get

        if (users == null || users.size() == 0) {
            return null;
        }

        UserRepresentation user = users.get(0);

        MappingsRepresentation mr = usersResource.get(user.getId()).roles().getAll();

        List<RoleRepresentation> rolesRepre = mr.getRealmMappings();
        List<String> roles = new ArrayList<String>();

        for (RoleRepresentation rr : rolesRepre) {

            // System.out.println("ROLES: " + rr.getName());
            roles.add(rr.getName());
        }

        RolesInfo ri = new RolesInfo(username, roles.toArray(new String[roles.size()]));

        return ri;
    }

    @Override
    public String[] getUsernamesByRol(String rol) throws Exception {

        RolesResource roleres = getKeyCloakConnectionForRoles();

        Set<UserRepresentation> userRep = roleres.get(rol).getRoleUserMembers();

        List<String> users = new ArrayList<String>();

        for (UserRepresentation ur : userRep) {
            users.add(ur.getUsername());
        }

        return users.toArray(new String[users.size()]);
    }

    public List<UserInfo> getUsersByPartialUserName(String partialusername) throws Exception {

        UsersResource usersResource = getKeyCloakConnectionForUsers();

        log.info("XYZ ZZZ KEYCLOAK::getUserInfoByUserName  ==>  " + usersResource);

        String search = partialusername;

        Integer firstResult = 0;

        Integer maxResults = 20;

        Boolean briefRepresentation = false;

        // log.info(" usersResource.count() => " + usersResource.count());

        List<UserRepresentation> users = usersResource.search(

                search,

                firstResult,

                maxResults,

                briefRepresentation

        );
        /*
         * @QueryParam("username") String username,
         * 
         * @QueryParam("firstName") String firstName,
         * 
         * @QueryParam("lastName") String lastName,
         * 
         * @QueryParam("email") String email,
         * 
         * @QueryParam("first") Integer firstResult,
         * 
         * @QueryParam("max") Integer maxResults,
         * 
         * @QueryParam("briefRepresentation"
         */

        // users.get(0).get

        List<UserInfo> us = new ArrayList<UserInfo>();

        if (users == null || users.size() == 0) {
            return us;
        }

        // UserRepresentation user = users.get(0);

        for (UserRepresentation user : users) {
            UserInfo ui = userRepresentationToUserInfo(user);
            us.add(ui);
        }

        return us;

    }

    public List<UserInfo> getUsersByNameAndSurnames(String nameOrSurname) throws Exception {

        List<UserInfo> l1 = getUsersByNameAndSurnames(nameOrSurname, null);

        List<UserInfo> l2 = getUsersByNameAndSurnames(null, nameOrSurname);

        Set<UserInfo> users = new TreeSet<UserInfo>(new Comparator<UserInfo>() {
            @Override
            public int compare(UserInfo o1, UserInfo o2) {
                return o1.getUsername().compareTo(o2.getUsername());
            }
        });

        if (l1 != null) {
            users.addAll(l1);
        }

        if (l2 != null) {
            users.addAll(l2);
        }

        ArrayList<UserInfo> list = new ArrayList<UserInfo>(users);

        return list;

    }

    protected List<UserInfo> getUsersByNameAndSurnames(String name, String surname)
            throws Exception {

        UsersResource usersResource = getKeyCloakConnectionForUsers();

        String username = "";
        String firstName = name;
        String lastName = surname;
        String email = "";

        Integer firstResult = 0;

        Integer maxResults = 20;

        Boolean briefRepresentation = false;

        // log.info(" usersResource.count() => " + usersResource.count());

        List<UserRepresentation> users = usersResource.search(

                username, firstName, lastName, email,

                firstResult,

                maxResults,

                briefRepresentation

        );
        /*
         * @QueryParam("username") String username,
         * 
         * @QueryParam("firstName") String firstName,
         * 
         * @QueryParam("lastName") String lastName,
         * 
         * @QueryParam("email") String email,
         * 
         * @QueryParam("first") Integer firstResult,
         * 
         * @QueryParam("max") Integer maxResults,
         * 
         * @QueryParam("briefRepresentation"
         */

        // users.get(0).get

        List<UserInfo> us = new ArrayList<UserInfo>();

        if (users == null || users.size() == 0) {
            return us;
        }

        // UserRepresentation user = users.get(0);

        for (UserRepresentation user : users) {
            UserInfo ui = userRepresentationToUserInfo(user);
            us.add(ui);
        }

        return us;

    }

}
