package org.fundaciobit.pluginsib.userinformation.keycloak;

import java.lang.reflect.Field;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.fundaciobit.pluginsib.userinformation.AbstractUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.SearchStatus;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
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
public class KeyCloakUserInformationPlugin extends AbstractUserInformationPlugin {

    private static final String PLUGINSIB_USERINFORMATION_BASE_PROPERTIES = IPLUGINSIB_BASE_PROPERTIES
            + "userinformation.";

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

    public static final String MAPPING_NIF_PROPERTY = MAPPING_PROPERTY + "administrationID";

    public static final String MINIMUM_CHARACTERS_TO_SEARCH_PROPERTY = KEYCLOAK_BASE_PROPERTY
            + "minimumcharacterstosearch";

    public static final String MAX_ALLOWED_NUMBER_OF_RESULTS_IN_PARTIAL_SEARCHES = KEYCLOAK_BASE_PROPERTY
            + "maxallowednumberofresultsinpartialsearches";

    private CacheNifUsername cache = new CacheNifUsername();

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

    @Override
    protected int getMinimumCharactersToSearch() {
        final int defaultValue = 3;
        String minStr = getProperty(MINIMUM_CHARACTERS_TO_SEARCH_PROPERTY);
        try {
            if (minStr != null && minStr.trim().length() != 0) {
                return Integer.parseInt(minStr);
            }
        } catch (NumberFormatException e) {
            log.warn(
                    "Propietat " + this.getPropertyKeyBase() + MINIMUM_CHARACTERS_TO_SEARCH_PROPERTY
                            + " ha de definir un sencer: " + e.getMessage(),
                    e);
        }
        return defaultValue;
    }

    protected int getMaxAllowedNumberOfResults() {
        final int defaultValue = 30;
        String minStr = getProperty(MAX_ALLOWED_NUMBER_OF_RESULTS_IN_PARTIAL_SEARCHES);
        try {
            if (minStr != null && minStr.trim().length() != 0) {
                return Integer.parseInt(minStr);
            }
        } catch (NumberFormatException e) {
            log.warn("Propietat " + this.getPropertyKeyBase()
                    + MAX_ALLOWED_NUMBER_OF_RESULTS_IN_PARTIAL_SEARCHES
                    + " ha de definir un sencer: " + e.getMessage(), e);
        }
        return defaultValue;
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

    @Override
    public long countAllUsers() throws Exception {
        UsersResource usersResource = getKeyCloakConnectionForUsers();
        Integer count = usersResource.count();

        if (count == null) {
            throw new Exception(
                    "La cridada a count() ha retornat un null per causes desconegudes.");
        }

        return count.longValue();
    }

    @Override
    public UserInfo getUserInfoByAdministrationID(String administrationID) throws Exception {

        {
            String username = cache.getUsernameByNif(administrationID);
            if (username != null) {
                return getUserInfoByUserName(username);
            }
        }

        UserInfo ui = null;

        // Si existeix la cache i aquesta es recent, llavors retornam null
        if (cache.isNifCacheComplete()) {
            return ui;
        }

        // Cerca un usuari a partir del NIF.
        // Hem de cercar-ho a lo bruto, en tots els usuaris
        UsersResource usersResource = getKeyCloakConnectionForUsers();

        final int step = 30;

        int start = 0;
        int total = 0;

        List<UserRepresentation> users;

        final String attributeUserNIF = getPropertyRequired(MAPPING_NIF_PROPERTY);

        while ((users = usersResource.list(start, step)) != null) {

            if (users.size() == 0) {
                break;
            }
            start = start + step;

            for (UserRepresentation ur : users) {
                total++;

                String nif = cache.getNifAttributeAndUpdateCache(attributeUserNIF, ur);
                if (nif == null) {
                    continue;
                }

                if (administrationID.equalsIgnoreCase(nif)) {
                    // OK
                    if (isDebug()) {
                        log.info("TROBAT USUARI !!!!!!! " + ur.getUsername());
                    }
                    ui = userRepresentationToUserInfo(ur);

                    return ui;

                }
            }
        }

        cache.setNifCacheComplete();
        if (isDebug()) {
            log.info("PROCESSADES TOTES LES PERSONES TOTAL PERSONES: " + total);
        }

        return ui;

    }

    @Override
    public UserInfo getUserInfoByUserName(String username) throws Exception {

        if (username == null || username.trim().length() == 0) {
            log.warn("getUserInfoByUserName():: Username is null or empty");
            return null;
        }

        UsersResource usersResource = getKeyCloakConnectionForUsers();

        List<UserRepresentation> users = usersResource.search(username);

        if (users == null || users.size() == 0) {
            return null;
        }

        final String usernameLowercase = username.toLowerCase();

        for (UserRepresentation user : users) {

            if (user.getUsername().toLowerCase().equals(usernameLowercase)) {

                UserInfo ui = userRepresentationToUserInfo(user);

                return ui;
            }
        }

        return null;

    }

    protected Set<String> mappingsAvailable = null;

    
    /**
     * Camps de UserInfo que excuim ja que venen amb l'API de keyCloak
     * Excluim "birthDate" i "creationDate"  ja que són Dates.
     */
    protected final HashSet<String> attributesToExclude = new HashSet<String>(Arrays.asList("id",
            "username", "email", "name", "socialNetworks", "attributes", "birthDate", "creationDate" 
    ));

    /**
     * 
     * @param user
     * @return
     * @throws Exception
     */
    protected UserInfo userRepresentationToUserInfo(UserRepresentation user) throws Exception {

        // XYZ ZZZ Debug Especial
        final boolean debug = false; // isDebug();

        if (debug) {

            log.info("ui.setEmail => " + user.getEmail());
            log.info("ui.setName => " + user.getFirstName());
            log.info("ui.setSurname1 => " + user.getLastName());
            log.info("ui.setUsername => " + user.getUsername());

        }

        UserInfo ui = new UserInfo();
        ui.setId(user.getId());
        ui.setEmail(user.getEmail());
        ui.setName(user.getFirstName());
        ui.setSurname1(user.getLastName());
        ui.setSurname2(null);
        ui.setUsername(user.getUsername());

        Map<String, List<String>> userAttributes = user.getAttributes();
        if (userAttributes != null && userAttributes.size() != 0) {

            if (mappingsAvailable == null) {
                /*
                 * FIELD => ]id,username,administrationID,name,surname1,surname2
                 * email,language,phoneNumber,password,gender,address,company,companyArea,
                 * companyDepartment,
                 * website,birthDate,creationDate,notes,socialNetworks,attributes[
                 */

                HashSet<String> tmp = new HashSet<String>();

                
                for (String field :  getAvailableUserInfoFields().keySet()) {
                    
                    if (!attributesToExclude.contains(field)) {
                        tmp.add(field);
                    }
                }

                mappingsAvailable = tmp;

            }

            ui.setAttributes(new HashMap<String, String>());

            for (String key : userAttributes.keySet()) {
                List<String> list = userAttributes.get(key);
                if (debug) {
                    log.info(" Attributes[" + key + "] => " + list.get(0));
                }
                ui.getAttributes().put(key, list.get(0));
            }

            for (String userInfoField : mappingsAvailable) {
                String attributeUser = getProperty(MAPPING_PROPERTY + userInfoField);
                if (attributeUser == null || attributeUser.trim().length() == 0) {
                    continue;
                }

                List<String> list = userAttributes.get(attributeUser);
                String attributeUserValue = list.get(0);

                if (debug) {
                    log.info(
                            " Posant al camp " + userInfoField + " el valor " + attributeUserValue);
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
                        log.error(" Error processant mapping de GENDER (-1, 0 o 1): "
                                + attributeUserValue);
                    }

                } else {
                    // CHECK DATES
                    field.set(ui, attributeUserValue);
                }

                ui.getAttributes().remove(attributeUser);

            }

        }
        return ui;
    }

    @Override
    public boolean isImplementedAuthenticationByUsernamePasword() {
        return true;
    }

    @Override
    public boolean authenticate(String username, String password) throws Exception {
        try {

            Keycloak keycloak = getKeyCloakConnectionUsernamePassword(username, password);

            // UsersResource usersResource =
            keycloak.realm(getPropertyRequired(REALM_PROPERTY)).users();

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
    public boolean isImplementedAuthenticationByCertificate() {
        return false;
    }

    @Override
    public boolean authenticate(X509Certificate certificate) throws Exception {
        throw new Exception("Do not implemented");
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

    /**
     * TODO XYZ ZZZ Optimitzar per retornar UserRepresentation
     * 
     * @param partialUsername
     * @return
     * @throws Exception
     */
    @Override
    public SearchUsersResult getUsersByPartialUserName(String partialUsername) throws Exception {

        

        SearchStatus ss = checkMinimumPartialString(partialUsername, "partialUsername");
        if (ss != null) {
            return new SearchUsersResult(ss);
        }

        List<UserRepresentation> usersThatMatch = new ArrayList<UserRepresentation>();

        final String partialUsernameLowercase = partialUsername.toLowerCase();
        final int maxAllowed = getMaxAllowedNumberOfResults();
        final boolean debug = isDebug();
        long startT = System.currentTimeMillis();

        if (cache.isNifCacheComplete()) {

            // La cache està completa, cercam per cache que serà més ràpid
            List<String> usernamesThatMatch = new ArrayList<String>();

            for (Entry<String, String> kv : cache.entrySet()) {
                if (kv.getValue().toLowerCase().indexOf(partialUsernameLowercase) != -1) {
                    usernamesThatMatch.add(kv.getValue());
                    if (usernamesThatMatch.size() > maxAllowed) {
                        SearchStatus smax = errorMassaResultats(maxAllowed);
                        return new SearchUsersResult(smax);
                    }
                }
            }

            if (debug) {
                log.info("La cerca en la cache de PARTIAL_USERNAME ]" + partialUsername
                        + "[ ha tardat " + (System.currentTimeMillis() - startT) + "ms");
            }

            // Cercar els usuaris que coincideixen per username
            startT = System.currentTimeMillis();

            List<UserInfo> users = new ArrayList<UserInfo>(usernamesThatMatch.size());

            for (String usrname : usernamesThatMatch) {
                try {
                    UserInfo ui = getUserInfoByUserName(usrname);
                    users.add(ui);
                } catch (Throwable th) {
                    log.warn("Error recuperant usuari amb username: " + usrname + ": "
                            + th.getMessage(), th);

                }
            }

            if (debug) {
                log.info("La recuperació de dades d'usuari en la cerca de PARTIAL_USERNAME ]"
                        + partialUsername + "[ ha tardat " + (System.currentTimeMillis() - startT)
                        + "ms");
            }

            return new SearchUsersResult(users);
        } else {
            
            UsersResource usersResource = getKeyCloakConnectionForUsers();

            final int step = 30;
            int start = 0;
            int total = 0;

            final Boolean briefRepresentation = false;
            final String attributeUserNIF = getPropertyRequired(MAPPING_NIF_PROPERTY);

            List<UserRepresentation> users;

            // Cerca per username i per email. Hem d'anar excloguent els que no s'ajustin
            // per username
            while ((users = usersResource.search(partialUsername, start, step,
                    briefRepresentation)) != null) {

                if (users == null || users.size() == 0) {
                    break;
                }

                start = start + step;
                total = total + users.size();

                for (UserRepresentation user : users) {

                    cache.getNifAttributeAndUpdateCache(attributeUserNIF, user);

                    if (user.getUsername().toLowerCase().indexOf(partialUsernameLowercase) != -1) {
                        usersThatMatch.add(user);
                        if (usersThatMatch.size() > maxAllowed) {
                            return new SearchUsersResult(errorMassaResultats(maxAllowed));
                        }
                    }
                }

                if (users.size() < step) {
                    break;
                }

            }

            if (debug) {
                log.info("La cerca de PARTIAL_USERNAME ]" + partialUsername + "[, cercant en "
                        + total + " usuaris, ha tardat " + (System.currentTimeMillis() - startT)
                        + "ms");
            }
        }

        List<UserInfo> us = new ArrayList<UserInfo>();
        for (UserRepresentation user : usersThatMatch) {
            UserInfo ui = userRepresentationToUserInfo(user);
            us.add(ui);
        }

        return new SearchUsersResult(us);

    }

    /**
     * TODO XYZ ZZZ Optimitzar per retornar UserRepresentation
     * 
     * @param partialEmail
     * @return
     * @throws Exception
     */
    public SearchUsersResult getUsersByPartialEmail(String partialEmail) throws Exception {

        UsersResource usersResource = getKeyCloakConnectionForUsers();

        SearchStatus ss = checkMinimumPartialString(partialEmail, "partialEmail");
        if (ss != null) {
            return new SearchUsersResult(ss);
        }

        List<UserRepresentation> usersThatMatch = new ArrayList<UserRepresentation>();

        final String partialEmailLowercase = partialEmail.toLowerCase();
        final int maxAllowed = getMaxAllowedNumberOfResults();
        final boolean debug = isDebug();
        long startT = System.currentTimeMillis();

        {

            final int step = 30;
            int start = 0;
            int total = 0;

            final Boolean briefRepresentation = false;
            final String attributeUserNIF = getPropertyRequired(MAPPING_NIF_PROPERTY);

            List<UserRepresentation> users;

            // Cerca per username i per email. Hem d'anar excloguent els que no s'ajustin
            // per email
            while ((users = usersResource.search(partialEmail, start, step,
                    briefRepresentation)) != null) {

                if (users == null || users.size() == 0) {
                    break;
                }

                start = start + step;
                total = total + users.size();

                for (UserRepresentation user : users) {

                    cache.getNifAttributeAndUpdateCache(attributeUserNIF, user);

                    if (user.getEmail().toLowerCase().indexOf(partialEmailLowercase) != -1) {
                        usersThatMatch.add(user);
                        if (usersThatMatch.size() > maxAllowed) {
                            return new SearchUsersResult(errorMassaResultats(maxAllowed));
                        }
                    }
                }

                if (users.size() < step) {
                    break;
                }

            }

            if (debug) {
                log.info("La cerca de PARTIAL_EMAIL ]" + partialEmail + "[, cercant en " + total
                        + " usuaris, ha tardat " + (System.currentTimeMillis() - startT) + "ms");
            }
        }

        List<UserInfo> us = new ArrayList<UserInfo>();
        for (UserRepresentation user : usersThatMatch) {
            UserInfo ui = userRepresentationToUserInfo(user);
            us.add(ui);
        }

        return new SearchUsersResult(us);

    }

    @Override
    public SearchUsersResult getUsersByPartialNameOrPartialSurnames(String partialNameOrSurname)
            throws Exception {

        SearchStatus ss = checkMinimumPartialString(partialNameOrSurname, "partialNameOrSurname");
        if (ss != null) {
            return new SearchUsersResult(ss);
        }

        final int maxAllowed = getMaxAllowedNumberOfResults();

        // final boolean debug = isDebug();

        Set<UserInfo> users = new TreeSet<UserInfo>(new UserInfoComparator());

        // Aquesta és la forma per fer un OR en la cerca a l'API
        SearchUsersResult sur1 = getUsersByPartialNameAndPartialSurname(users, partialNameOrSurname,
                null, maxAllowed);
        if (sur1 != null) { // null significa OK
            return sur1;
        }

        SearchUsersResult sur2 = getUsersByPartialNameAndPartialSurname(users, null,
                partialNameOrSurname, maxAllowed);
        if (sur2 != null) { // null significa OK
            return sur2;
        }

        return new SearchUsersResult(new ArrayList<UserInfo>(users));

    }

    /**
     * 
     * @param allUsers
     * @param firstName
     * @param lastName
     * @param maxAllowed
     * @return
     * @throws Exception
     */
    protected SearchUsersResult getUsersByPartialNameAndPartialSurname(Set<UserInfo> allUsers,
            String firstName, String lastName, int maxAllowed) throws Exception {

        final String username = null;
        final String email = null;

        final Integer firstResult = 0;

        final Integer maxResults = maxAllowed + 1;

        final Boolean briefRepresentation = false;

        UsersResource usersResource = getKeyCloakConnectionForUsers();
        List<UserRepresentation> users = usersResource.search(username, firstName, lastName, email,
                firstResult, maxResults, briefRepresentation);

        if (users != null && users.size() != 0) {

            if (users.size() > maxAllowed) {
                return new SearchUsersResult(errorMassaResultats(maxAllowed));
            }

            for (UserRepresentation user : users) {
                UserInfo ui = userRepresentationToUserInfo(user);
                allUsers.add(ui);

                if (allUsers.size() > maxAllowed) {
                    return new SearchUsersResult(errorMassaResultats(maxAllowed));
                }
            }
        }

        return null; // OK

    }

    

    @Override
    public SearchUsersResult getUsersByPartialAdministrationID(String partialAdministratorID)
            throws Exception {

        SearchStatus ss = checkMinimumPartialString(partialAdministratorID,
                "partialAdministratorID");
        if (ss != null) {
            return new SearchUsersResult(ss);
        }

        final int maxAllowed = getMaxAllowedNumberOfResults();

        final String partialNif = partialAdministratorID.toLowerCase();

        final boolean debug = isDebug();

        if (cache.isNifCacheComplete()) {

            // La cache està completa, cercam per cache que serà més ràpid

            List<String> usernamesThatMatch = new ArrayList<String>();

            long start = System.currentTimeMillis();

            for (Entry<String, String> kv : cache.entrySet()) {
                if (kv.getKey().toLowerCase().indexOf(partialNif) != -1) {
                    usernamesThatMatch.add(kv.getValue());
                    if (usernamesThatMatch.size() > maxAllowed) {
                        SearchStatus smax = errorMassaResultats(maxAllowed);
                        return new SearchUsersResult(smax);
                    }
                }
            }

            if (debug) {
                log.info("La cerca en la cache de PARTIAL_NIF ]" + partialAdministratorID
                        + "[ ha tardat " + (System.currentTimeMillis() - start) + "ms");
            }

            // Cercar els usuaris que coincideixen per username
            start = System.currentTimeMillis();

            List<UserInfo> users = new ArrayList<UserInfo>(usernamesThatMatch.size());

            for (String usrname : usernamesThatMatch) {
                try {
                    UserInfo ui = getUserInfoByUserName(usrname);
                    users.add(ui);
                } catch (Throwable th) {
                    log.warn("Error recuperant usuari amb username: " + usrname + ": "
                            + th.getMessage(), th);

                }
            }

            if (debug) {
                log.info("La recuperació de dades d'usuari en la cerca de PARTIAL_NIF ]"
                        + partialAdministratorID + "[ ha tardat "
                        + (System.currentTimeMillis() - start) + "ms");
            }

            return new SearchUsersResult(users);

        } else {

            // No tenim cache, pitjor cas, hem de cercar de per tots els usuaris

            long startT = System.currentTimeMillis();

            UsersResource usersResource = getKeyCloakConnectionForUsers();

            final int step = 30;

            int start = 0;
            int total = 0;

            List<UserRepresentation> users;

            List<UserRepresentation> usersThatMatch = new ArrayList<UserRepresentation>();

            final String attributeUserNIF = getProperty(MAPPING_PROPERTY + "administrationID");

            while ((users = usersResource.list(start, step)) != null) {

                if (users.size() == 0) {
                    break;
                }

                // Debug FULL
                /*
                 * if (debug) { log.info("  BUCLE   " + start + " - " + (start + step)); }
                 */

                start = start + step;

                if (attributeUserNIF == null || attributeUserNIF.trim().length() == 0) {

                    String msg = "La cerca empant Administration ID no és posible,"
                            + " ja que no s'ha definit cap propietat mapping emprant la clau "
                            + getPropertyKeyBase() + MAPPING_PROPERTY + "administrationID";
                    log.error(msg);
                    SearchStatus error = new SearchStatus(SearchStatus.RESULT_CLIENT_ERROR, msg);
                    return new SearchUsersResult(error);

                }

                for (UserRepresentation ur : users) {
                    total++;

                    String nif = cache.getNifAttributeAndUpdateCache(attributeUserNIF, ur);

                    if (nif == null) {
                        continue;
                    }

                    if (nif.toLowerCase().indexOf(partialNif) != -1) {
                        usersThatMatch.add(ur);
                        if (usersThatMatch.size() > maxAllowed) {
                            SearchStatus smax = errorMassaResultats(maxAllowed);
                            return new SearchUsersResult(smax);
                        }
                    }

                }
            }

            // S'ha cercar per tots els usuaris, per això indicam que la cache està completa
            cache.setNifCacheComplete();

            // Passar resultats a bean API
            List<UserInfo> usersInfo = new ArrayList<UserInfo>(usersThatMatch.size());
            for (UserRepresentation userRepresentation : usersThatMatch) {
                usersInfo.add(userRepresentationToUserInfo(userRepresentation));
            }

            if (debug) {
                log.info("La recuperació de dades d'usuari en la cerca de PARTIAL_NIF ]"
                        + partialAdministratorID + "[,\n cercant sobre " + total + " usuaris,"
                        + " ha tardat " + (System.currentTimeMillis() - startT) + "ms");
            }

            return new SearchUsersResult(usersInfo);

        }
    }

    @Override
    public boolean isImplementedUsersByPartialValuesAnd() {
        return true;
    }

    /**
     * Executa una cerca en els usuaris a partir dels valors parcials de username,
     * nom, llinatge, email i nif. Es realitza una intersecció del resultat de totes
     * les subcerques (AND). Si algun valor val null, llavors s'ignora la cerca per
     * aquell camp.
     * 
     * @param usernamePartial
     * @param firstNamePartial
     * @param lastNamePartial
     * @param emailPartial
     * @param administrationIDPartial
     * @return
     * @throws Exception
     */
    @Override
    public SearchUsersResult getUsersByPartialValuesAnd(String usernamePartial,
            String firstNamePartial, String lastNamePartial, String emailPartial,
            String administrationIDPartial) throws Exception {

        long startT = System.currentTimeMillis();

        // Cercam la mitja de longitud de les cadenes de cerca.
        // Aquesta ha de superar el mínim permés
        final String[] values = { usernamePartial, firstNamePartial, lastNamePartial, emailPartial,
                administrationIDPartial };
        final String[] field = { "usernamePartial", "firstNamePartial", "lastNamePartial",
                "emailPartial", "administrationIDPartial" };
        float suma = 0;
        float count = 0;
        String camps = "";
        final int minimumCharachtersToSearch = getMinimumCharactersToSearch();
        for (int j = 0; j < values.length; j++) {
            String v = values[j];
            if (v != null && v.trim().length() != 0) {
                suma = suma + v.length();
                count = count + 1;
                if (v.length() < minimumCharachtersToSearch) {
                    camps = camps + "," + field[j];
                }
            }
        }

        if (count == 0) {
            String searchString = null;
            SearchStatus ss = errorCadenaDeCercaNullBuida(searchString);

            return new SearchUsersResult(ss);
        }

        final float mitja = suma / count;

        if (mitja < minimumCharachtersToSearch) {
            SearchStatus ss = errorCadenaDeCercaMassaCurta(mitja, minimumCharachtersToSearch, "*");

            return new SearchUsersResult(ss);
        }

        // Si els valors username, nom, llinatge i email són null, llavors només cercam
        // per Nif
        if (empty(usernamePartial) && empty(firstNamePartial) && empty(lastNamePartial)
                && empty(emailPartial)) {
            return getUsersByPartialAdministrationID(administrationIDPartial);
        }

        // Cercam en KeyCloak per usernamePartial, firstNamePartial, lastNamePartial i
        // emailPartial

        final int maxAllowed = getMaxAllowedNumberOfResults();

        final Integer firstResult = 0;

        final Integer maxResults = maxAllowed + 1;

        final Boolean briefRepresentation = false;

        final boolean debug = isDebug();

        UsersResource usersResource = getKeyCloakConnectionForUsers();
        List<UserRepresentation> users = usersResource.search(usernamePartial, firstNamePartial,
                lastNamePartial, emailPartial, firstResult, maxResults, briefRepresentation);

        // Massa resultats ????
        if (users.size() > maxAllowed) {
            SearchStatus ss = errorMassaResultats(maxAllowed);
            return new SearchUsersResult(ss);
        }

        // Aplicar filtre de NIF
        List<UserRepresentation> usersThatMatch;
        if (administrationIDPartial == null) {
            usersThatMatch = users;
        } else {

            final String attributeUserNIF = getPropertyRequired(MAPPING_NIF_PROPERTY);
            usersThatMatch = new ArrayList<UserRepresentation>();

            for (UserRepresentation ur : users) {

                String nif = cache.getNifAttributeAndUpdateCache(attributeUserNIF, ur);

                if (nif == null) {
                    // En principi l'acceptam ..., si el nif de l'usuari val null
                    // suposarem que no podem fer la cerca sobre aquest valor i l'acceptam
                    usersThatMatch.add(ur);
                } else {
                    if (nif.indexOf(administrationIDPartial) != -1) {
                        // S'ajusta
                        usersThatMatch.add(ur);
                    }
                }
            }
        }

        List<UserInfo> usersInfo = new ArrayList<UserInfo>(usersThatMatch.size());
        for (UserRepresentation userRepresentation : usersThatMatch) {
            usersInfo.add(userRepresentationToUserInfo(userRepresentation));
        }

        if (debug) {
            log.info("La recuperació de dades d'usuari en la cerca de PARTIAL_VALUES_AND ]"
                    + usernamePartial + "[, ha tardat " + (System.currentTimeMillis() - startT)
                    + "ms");
        }

        return new SearchUsersResult(usersInfo);

    }

    @Override
    public boolean isImplementedUsersByPartialValuesOr() {
        return true;
    }

    /**
     * Executa una cerca en els usuaris a partir dels valors parcials de username,
     * nom, llinatge, email i nif. Es realitza una unió del resultat de totes les
     * subcerques (AND). El resultat no inclou usuaris repetits. Si algun valor val
     * null, llavors s'ignora la cerca per aquell camp.
     * 
     * @param usernamePartial
     * @param firstNamePartial
     * @param lastNamePartial
     * @param emailPartial
     * @param administrationIDPartial
     * @return
     * @throws Exception
     */
    @Override
    public SearchUsersResult getUsersByPartialValuesOr(String usernamePartial,
            String firstNamePartial, String lastNamePartial, String emailPartial,
            String administrationIDPartial) throws Exception {

        // Cercam la cadena de cerca dins username o email
        final int maxAllowed = getMaxAllowedNumberOfResults();

        // final boolean debug = isDebug();

        Set<UserInfo> usersThatMatch = new TreeSet<UserInfo>(new UserInfoComparator());

        // ========= USERNAME
        if (!empty(usernamePartial)) {
            SearchUsersResult un = getUsersByPartialUserName(usernamePartial);
            if (un.getStatus().getResultCode() != SearchStatus.RESULT_OK) {
                return un;
            }
            usersThatMatch.addAll(un.getUsers());
        }

        // ========= EMAIL
        if (!empty(emailPartial)) {
            SearchUsersResult un = getUsersByPartialEmail(emailPartial);
            if (un.getStatus().getResultCode() != SearchStatus.RESULT_OK) {
                return un;
            }
            usersThatMatch.addAll(un.getUsers());
            if (usersThatMatch.size() > maxAllowed) {
                return new SearchUsersResult(errorMassaResultats(maxAllowed));
            }
        }

        // ========= NAME (firsname)
        if (!empty(firstNamePartial)) {
            SearchUsersResult sur1 = getUsersByPartialNameAndPartialSurname(usersThatMatch,
                    firstNamePartial, null, maxAllowed);
            if (sur1 != null) { // null significa OK
                return sur1;
            }
        }

        // =========== SURNAME (lastname)
        if (!empty(lastNamePartial)) {
            SearchUsersResult sur2 = getUsersByPartialNameAndPartialSurname(usersThatMatch, null,
                    lastNamePartial, maxAllowed);
            if (sur2 != null) { // null significa OK
                return sur2;
            }
        }

        // =========== ADMINISTRATION ID
        if (!empty(administrationIDPartial)) {

            SearchUsersResult sur = getUsersByPartialAdministrationID(administrationIDPartial);
            if (sur.getStatus().getResultCode() != SearchStatus.RESULT_OK) {
                return sur;
            }
            usersThatMatch.addAll(sur.getUsers());
            if (usersThatMatch.size() > maxAllowed) {
                return new SearchUsersResult(errorMassaResultats(maxAllowed));
            }
        }

        List<UserInfo> usersInfo = new ArrayList<UserInfo>(usersThatMatch.size());
        usersInfo.addAll(usersThatMatch);

        return new SearchUsersResult(usersInfo);

    }

    public static class UserRepresentationComparator implements Comparator<UserRepresentation> {
        @Override
        public int compare(UserRepresentation o1, UserRepresentation o2) {
            return o1.getUsername().compareTo(o2.getUsername());
        }
    }

    /*
     * public void searchByAttributes() throws Exception {
     * 
     * UsersResource usersResource = getKeyCloakConnectionForUsers();
     * 
     * List<UserRepresentation> users;
     * 
     * 
     * users = usersResource.search("nif:12345678X");
     * 
     * if (users.size() == 0) { log.info("LLISTA BUIDA => "); } else { for
     * (UserRepresentation ur : users) { log.info("searchByAttributes => " +
     * ur.getEmail() + "  " + " " + ur.getFirstName()); } }
     * 
     * }
     */

}
