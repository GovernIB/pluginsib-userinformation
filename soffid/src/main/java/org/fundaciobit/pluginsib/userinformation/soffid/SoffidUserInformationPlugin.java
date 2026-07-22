package org.fundaciobit.pluginsib.userinformation.soffid;

import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.fundaciobit.pluginsib.userinformation.AbstractUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.SearchStatus;
import org.fundaciobit.pluginsib.userinformation.SearchUsersResult;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.soffid.beans.Attributes;
import org.fundaciobit.pluginsib.userinformation.soffid.beans.Resource;
import org.fundaciobit.pluginsib.userinformation.soffid.beans.SoffidUserResults;
import org.fundaciobit.pluginsib.utils.templateengine.TemplateEngine;
import org.jboss.logging.Logger;

import com.unboundid.scim2.common.exceptions.NotImplementedException;
import com.unboundid.scim2.common.messages.ErrorResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

/**
 * Plugin per a la informació d'usuaris cridant a Soffid.
 * @author anadal
 * 7 jul 2025 9:43:33
 */
public class SoffidUserInformationPlugin extends AbstractUserInformationPlugin {

    private static final String SOFFID_BASE_PROPERTY = USERINFORMATION_BASE_PROPERTY + "soffid.";

    public static final String SERVER_URL_PROPERTY = SOFFID_BASE_PROPERTY + "serverurl";
    public static final String USERNAME_PROPERTY = SOFFID_BASE_PROPERTY + "username";

    public static final String PASSWORD_PROPERTY = SOFFID_BASE_PROPERTY + "password";

    public static final String ENTORN_PROPERTY = SOFFID_BASE_PROPERTY + "entorn";

    public static final String EMAIL_EL = SOFFID_BASE_PROPERTY + "email_el";

    public static final String MINIMUM_CHARACTERS_TO_SEARCH_PROPERTY = SOFFID_BASE_PROPERTY
            + "minimumcharacterstosearch";

    public static final String MAX_ALLOWED_NUMBER_OF_RESULTS_IN_PARTIAL_SEARCHES = SOFFID_BASE_PROPERTY
            + "maxallowednumberofresultsinpartialsearches";

    public static final String DEBUG_PROPERTY = SOFFID_BASE_PROPERTY + "debug";

    public static final String DEBUG_REST_PROPERTY = SOFFID_BASE_PROPERTY + "debugrest";

    //private CacheNifUsername cache = new CacheNifUsername();

    /**
     * 
     */
    public SoffidUserInformationPlugin() {
        super();
    }

    /**
     * @param propertyKeyBase
     */
    public SoffidUserInformationPlugin(String propertyKeyBase) {
        super(propertyKeyBase);
    }

    /**
     * @param propertyKeyBase
     * @param properties
     */
    public SoffidUserInformationPlugin(String propertyKeyBase, Properties properties) {
        super(propertyKeyBase, properties);
    }

    protected boolean isDebug() {
        String debug = getProperty(DEBUG_PROPERTY, "false");
        return "true".equals(debug);
    }

    protected boolean isDebugRest() {
        String debug = getProperty(DEBUG_REST_PROPERTY, "false");
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
            final String prop = this.getPropertyKeyBase() + MINIMUM_CHARACTERS_TO_SEARCH_PROPERTY;
            log.warn("Propietat " + prop + " ha de definir un sencer: " + e.getMessage(), e);
        }
        return defaultValue;
    }

    protected int getMaxAllowedNumberOfResults() {
        final int defaultValue = 100;
        String minStr = getProperty(MAX_ALLOWED_NUMBER_OF_RESULTS_IN_PARTIAL_SEARCHES);
        try {
            if (minStr != null && minStr.trim().length() != 0) {
                return Integer.parseInt(minStr);
            }
        } catch (NumberFormatException e) {
            log.warn("Propietat " + this.getPropertyKeyBase() + MAX_ALLOWED_NUMBER_OF_RESULTS_IN_PARTIAL_SEARCHES
                    + " ha de definir un sencer: " + e.getMessage(), e);
        }
        return defaultValue;
    }

    @Override
    public long countAllUsers() throws Exception {

        String urlOperation = "/User";

        SoffidUserResults sur = callToURL(urlOperation, SoffidUserResults.class);

        long total = sur.getTotalResults();

        return total;
    }

    @Override
    public boolean isImplementedUserInfoByAdministrationID() {
        return true;
    }

    @Override
    public UserInfo getUserInfoByAdministrationID(String administrationID) throws Exception {

        if (administrationID == null || administrationID.trim().length() == 0) {
            log.warn("getUserInfoByAdministrationID():: administrationID is null or empty");
            return null;
        }

        administrationID = administrationID.trim().toUpperCase();

        String urlOperation = "/User?filter=attributes.NIF eq \"" + administrationID + "\"";

        SoffidUserResults sur = callToURL(urlOperation, SoffidUserResults.class);

        if (isDebug()) {
            log.debug("getUserInfoByAdministrationID():: NIF: " + administrationID + " - Resultats:\n" + sur);
        }

        if (sur.getTotalResults() > 1) {
            throw new Exception("Hi ha més d'un usuari amb NIF: " + administrationID
                    + ". Revisi si aquest NIF és correcte i està sencer.");
        }

        return soffidUserResultToUserInfo(sur, urlOperation);

    }
    
    
 // Camp de classe (thread-safe, reutilitzable)
    private static final com.fasterxml.jackson.databind.ObjectMapper JACKSON_MAPPER =
        new com.fasterxml.jackson.databind.ObjectMapper()
            .configure(
                com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);

    protected <T> T callToURL(String urlOperation, Class<T> classe) throws Exception {

        String urlbase = getPropertyRequired(SERVER_URL_PROPERTY);

        urlbase = urlbase.trim();

        if (urlbase.endsWith("/")) {
            urlbase = urlbase.substring(0, urlbase.length() - 1);
        }

        String fullUrl = urlbase + urlOperation;

        String username = getPropertyRequired(USERNAME_PROPERTY);
        String password = getPropertyRequired(PASSWORD_PROPERTY);

        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        ClientBuilder clientBuilder = ClientBuilder.newBuilder();

        // Forçam Jackson com a provider JSON, independentment de l'entorn/JDK
        clientBuilder.register(org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider.class);

        if (isDebugRest()) {
            // Registrar el filtre de logging propi (inclou body de request i response)
            clientBuilder.register(new LoggingClientFilter(log));
        }

        Client client = clientBuilder.build();

        WebTarget target = client.target(fullUrl);

        Response response = target.request("application/scim+json")
                .header(javax.ws.rs.core.HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials).get();

        final int status = response.getStatus();

        if (status == 200) {

            /*
            String hola = response.readEntity(String.class);
            
            System.out.println("Resposta: " + hola);
            
            
            Thread.sleep(10000);
            */
/*
            T value = response.readEntity(classe);
            response.close(); // You should close connections
            return value;
            */
            
            String json = response.readEntity(String.class); // sempre funciona: text pla
            response.close();
            try {
                return JACKSON_MAPPER.readValue(json, classe);
            } catch (Exception e) {
                throw new Exception("No s'ha pogut deserialitzar la resposta amb Jackson: "
                    + e.getMessage(), e);
            }
            
            
        } else {

            log.error("callToURL():: Error al cridar a la URL: " + fullUrl + " amb codi d'error " + response.getStatus()
                    + ": " + response.getStatusInfo().getReasonPhrase());
            if (status == 400) {
                ErrorResponse error = null;
                try {
                    error = response.readEntity(ErrorResponse.class);
                } catch (Throwable e) {
                    // No feim res
                    if (isDebug()) {
                        log.error("Error al llegir ErrorResponse.class de la resposta: " + e.getMessage(), e);
                    }
                }

                if (error != null) {
                    throw new Exception(error.getDetail());
                }
            }

            throw new Exception("Failed : HTTP error code " + status + ": " + response.getStatusInfo().getReasonPhrase()
                    + "\n" + response.readEntity(String.class));
        }

    }

    @Override
    public UserInfo getUserInfoByUserName(String username) throws Exception {

        if (username == null || username.trim().length() == 0) {
            log.warn("getUserInfoByUserName():: Username is null or empty");
            return null;
        }

        String urlOperation = "/User?filter=userName eq \"" + username + "\"";

        SoffidUserResults sur = callToURL(urlOperation, SoffidUserResults.class);

        if (isDebug()) {
            log.debug("getUserInfoByUserName():: Username: " + username + " - Resultats:\n" + sur);
        }

        return soffidUserResultToUserInfo(sur, urlOperation);

    }

    protected SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected List<UserInfo> soffidUserResultToUserInfos(SoffidUserResults user) throws Exception {

        List<Resource> resources = user.getResources();

        if (resources == null || resources.size() == 0) {
            return new ArrayList<UserInfo>(0);
        }

        List<UserInfo> userInfos = new ArrayList<UserInfo>(resources.size());
        for (Resource resource : user.getResources()) {
            userInfos.add(resourceToUserInfo(resource));
        }
        return userInfos;
    }

    protected UserInfo soffidUserResultToUserInfo(SoffidUserResults user, String query) throws Exception {

        // XYZ ZZZ Debug Especial
        //final boolean debug = false; // isDebug();

        // https://github.com/pingidentity/scim2/blob/master/scim2-sdk-common/src/main/java/com/unboundid/scim2/common/types/UserResource.java
        // ListResponse<UserResource>  user

        if (user == null || user.getResources() == null || user.getResources().size() == 0) {
            if (isDebug()) {
                log.info("soffidUserResultToUserInfo():: No hi ha resultats per a la consulta: ]" + query + "[");
            }

            return null;
        }

        Resource resource = user.getResources().get(0);

        UserInfo ui = resourceToUserInfo(resource);

        return ui;
    }

    protected UserInfo resourceToUserInfo(Resource resource) throws ParseException {
        UserInfo ui = new UserInfo();
        ui.setId(String.valueOf(resource.getId()));

        String fullName;
        if (resource.getFullName() != null) {
            fullName = resource.getFullName();
        } else if (resource.getUserFullName() != null) {
            fullName = resource.getUserFullName();
        } else {
            fullName = null;
        }

        ui.setEmail(resource.getEmailAddress());

        ui.setName(resource.getFirstName());
        ui.setSurname1(resource.getLastName());
        ui.setSurname2(resource.getMiddleName());

        if (ui.getName() == null && ui.getSurname1() == null && ui.getSurname2() == null && fullName != null) {
            // Si no tenim nom ni cognoms, però sí el nom complet, llavors
            // assignam el nom complet com a nom

            String[] parts = fullName.split(" ");

            //            if (fullName.contains("Montenegro")) {
            //                System.err.println("]" + fullName + "[ " + parts.length );
            //            }

            if (parts.length == 3) {
                ui.setName(parts[0]);
                ui.setSurname1(parts[1]);
                ui.setSurname2(parts[2]);
            } else if (parts.length == 4) {
                ui.setName(parts[0] + " " + parts[1]);
                ui.setSurname1(parts[2]);
                ui.setSurname2(parts[3]);
            } else {
                ui.setName(fullName);
                ui.setSurname1(null);
                ui.setSurname2(null);
            }

        }

        {
            String username = getUsernameOfResource(resource);
            ui.setUsername(username);
        }

        ui.setCompanyDepartment(resource.getPrimaryGroup());
        if (ui.getCompanyDepartment() == null) {
            ui.setCompanyDepartment(resource.getUserGroupCode());
        }

        Map<String, String> attributes = new LinkedHashMap<String, String>();
        if (resource.getAttributes() != null) {

            for (Map.Entry<String, String> att : resource.getAttributes().entrySet()) {
                String key = att.getKey();
                String value = att.getValue();

                switch (key) {
                    case Attributes.E_MAIL_CONTACTE:
                        if (ui.getEmail() == null) {
                            ui.setEmail(value);
                        }
                    break;

                    case Attributes.NIF:
                        ui.setAdministrationID(value);
                    break;

                    case Attributes.PHONE:
                        ui.setMobileNumber(value);
                    break;

                    case Attributes.PSEUDONIM:
                        if (value != null && value.trim().length() != 0) {
                            ui.setPseudonyms(new HashSet<String>(Arrays.asList(value)));
                        }

                    default:
                        // Ho ficam a la llista d'atributs no controlats
                        if (isDebug()) {
                            attributes.put(key, value);
                        }
                }
            }

        }

        if (resource.getCreatedDate() != null) {
            ui.setCreationDate(SDF.parse(resource.getCreatedDate()));
        }

        if (resource.getCreatedDate() == null) {
            ui.setCreationDate(SDF.parse(resource.getStartDate()));
        }

        if (resource.getUserType() != null) {
            attributes.put("userType", resource.getUserType());
        }

        if (fullName != null) {
            attributes.put("fullName", fullName);
        }

        if (attributes.size() != 0) {
            ui.setAttributes(attributes);
        }

        String emailEL = getProperty(EMAIL_EL);
        if (emailEL != null && emailEL.trim().length() != 0) {
            try {
                Map<String, Object> parameters = new LinkedHashMap<String, Object>();
                parameters.put("user", ui);
                String email = TemplateEngine.processExpressionLanguage(emailEL, parameters);
                ui.setEmail(email);
            } catch (Exception e) {
                log.warn("Error al processar l'expression language per a l'email: " + emailEL + " - " + e.getMessage(),
                        e);
            }
        }

        return ui;
    }

    protected String getUsernameOfResource(Resource resource) {
        String username = resource.getUserName();
        if (username == null) {
            username = resource.getUserCode();
        }
        return username;
    }

    protected boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    @Override
    public boolean isImplementedAuthenticationByUsernamePasword() {
        return false;
    }

    @Override
    public boolean authenticate(String username, String password) throws Exception {
        // TODO
        throw new NotImplementedException(
                "Mètode autenticate(usr, pwd) no implementat. Per favor consulta mètode isImplementedAuthenticationByUsernamePasword()");
    }

    @Override
    public boolean isImplementedAuthenticationByCertificate() {
        return false;
    }

    @Override
    public boolean authenticate(X509Certificate certificate) throws Exception {
        throw new NotImplementedException(
                "Mètode autenticate(usr, pwd) no implementat. Per favor consulta mètode isImplementedAuthenticationByCertificate()");
    }

    @Override
    public String[] getAllUsernames() throws Exception {
        // User SoffidFullUserInformationPlugin
        throw new Exception("getAllUsernames() no està implementat en aquest plugin.");
    }

    protected List<Resource> consultaPaginada(String urlOperationBase, boolean debug, boolean checkMaxValuesAllowed)
            throws Exception, ExceededMaximumAllowedResultsException {
        List<Resource> results = null;

        int total = -1;

        int startIndex = 1;
        do {

            String urlOperation = urlOperationBase + "&startIndex=" + startIndex;

            SoffidUserResults sur = callToURL(urlOperation, SoffidUserResults.class);

            if (startIndex == 1) {
                total = sur.getTotalResults();
                if (checkMaxValuesAllowed) {
                    final int maxAllowed = getMaxAllowedNumberOfResults();
                    if (total > maxAllowed) {
                        SearchStatus smax = errorMassaResultats(maxAllowed, total);
                        throw new ExceededMaximumAllowedResultsException(smax);
                    }
                }

                results = new ArrayList<Resource>(total);
            }

            results.addAll(sur.getResources());

            if (debug) {
                log.info("Consulta Paginada: " + urlOperation + " - Resultats: " + sur.getTotalResults() + " - Inici: "
                        + startIndex + "/" + total);
            }

            if (sur.getItemsPerPage() == null) {
                startIndex = total + 1; // No hi ha més resultats
            } else {
                startIndex = sur.getItemsPerPage() + startIndex;
            }

        } while (startIndex < total);
        return results;
    }

    @Override
    public boolean isImplementedRolesQueries() {
        return true;
    }

    @Override
    public RolesInfo getRolesByUsername(String username) throws Exception {

        /**
        * PRO: http://ssoffidiam1.caib.es:8080/soffid/webservice/scim2/v1/RoleAccount?filter=userCode+eq+"x00000000"+and+enabled+eq+true+and+system+eq+"weblogic"
        PRE/SE: http://ssoffidiam1.caib.es:8080/soffid/webservice/scim2/v1/RoleAccount?filter=userCode+eq+"x00000000"+and+enabled+eq+true+and+system+eq+"weblogic-pre"
        DES: http://ssoffidiam1.caib.es:8080/soffid/webservice/scim2/v1/RoleAccount?filter=userCode+eq+"x00000000"+and+enabled+eq+true+and+system+eq+"web-des"
        */

        String entorn = getPropertyRequired(ENTORN_PROPERTY);

        final boolean debug = isDebug();
        if (debug) {
            log.info("getRolesByUsername() => Entorn: " + entorn);
        }

        String urlOperation = "/RoleAccount?filter=userCode+eq+\"" + username
                + "\"+and+enabled+eq+true+and+system+eq+\"" + entorn + "\"";

        //SoffidUserResults sur = callToURL(urlOperation, SoffidUserResults.class);

        List<Resource> resources = consultaPaginada(urlOperation, debug, false);

        //String sur = callToURL(urlOperation, String.class);
        String[] roles = new String[resources.size()];
        int count = 0;
        for (Resource resource : resources) {
            roles[count] = resource.getRoleName();
            count++;
        }

        RolesInfo ri = new RolesInfo(username, roles);

        return ri;
    }

    @Override
    public UserInfo[] getUserInfoByRol(String rol) throws Exception {

        /*
        PRO: http://ssoffidiam1.caib.es:8080/soffid/webservice/scim2/v1/RoleAccount?filter=roleName+eq+"NOT_USER"+and+enabled+eq+true+and+system+eq+"weblogic"
            PRE/SE: http://ssoffidiam1.caib.es:8080/soffid/webservice/scim2/v1/RoleAccount?filter=roleName+eq+"NOT_USER"+and+enabled+eq+true+and+system+eq+"weblogic-pre"
            DES: http://ssoffidiam1.caib.es:8080/soffid/webservice/scim2/v1/RoleAccount?filter=roleName+eq+"NOT_USER"+and+enabled+eq+true+and+system+eq+"web-des"
        */

        String entorn = getPropertyRequired(ENTORN_PROPERTY);

        final boolean debug = isDebug();
        if (debug) {
            log.info("getUserInfoByRol() => Entorn: " + entorn);
        }

        String urlOperation = "/RoleAccount?sortBy=userCode&filter=roleName+eq+\"" + rol
                + "\"+and+enabled+eq+true+and+system+eq+\"" + entorn + "\"";

        List<Resource> resources = consultaPaginada(urlOperation, debug, false);

        //String sur = callToURL(urlOperation, String.class);
        UserInfo[] users = new UserInfo[resources.size()];
        int count = 0;
        for (Resource resource : resources) {
            users[count] = resourceToUserInfo(resource);
            count++;
        }

        return users;

    }

    @Override
    public String[] getUsernamesByRol(String rol) throws Exception {

        String entorn = getPropertyRequired(ENTORN_PROPERTY);

        final boolean debug = isDebug();
        if (debug) {
            log.info("getUsernamesByRol() => Entorn: " + entorn);
        }

        String urlOperation = "/RoleAccount?sortBy=userCode&filter=roleName+eq+\"" + rol
                + "\"+and+enabled+eq+true+and+system+eq+\"" + entorn + "\"";

        List<Resource> resources = consultaPaginada(urlOperation, debug, false);

        //String sur = callToURL(urlOperation, String.class);
        String[] users = new String[resources.size()];
        int count = 0;
        for (Resource resource : resources) {
            users[count] = getUsernameOfResource(resource);
            count++;
        }

        return users;

    }

    /**
     * 
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

        // https://intranet.caib.es/soffid/webservice/scim2/v1/User?filter=userName co "u885"

        final String urlOperation = "/User?filter=userName co \"" + partialUsername + "\"";

        try {
            List<Resource> resources = consultaPaginada(urlOperation, isDebug(), true);

            List<UserInfo> users = resourcesToUserInfoList(resources);

            return new SearchUsersResult(users);

        } catch (ExceededMaximumAllowedResultsException e) {
            return new SearchUsersResult(e.getSmax());
        }

        /*        SoffidUserResults results = callToURL(urlOperation, SoffidUserResults.class);
        
        final int maxAllowed = getMaxAllowedNumberOfResults();
        
        if (results.getTotalResults() > maxAllowed) {
            SearchStatus smax = errorMassaResultats(maxAllowed, results.getTotalResults());
            return new SearchUsersResult(smax);
        }
        
        List<UserInfo> us = soffidUserResultToUserInfos(results);
        
        return new SearchUsersResult(us);
        */
    }

    /**
     * TODO PER ARA NO FUNCIONA
     * 
     * @param partialEmail
     * @return
     * @throws Exception
     */
    @Override
    public SearchUsersResult getUsersByPartialEmail(String partialEmail) throws Exception {

        throw new NotImplementedException("Mètode getUsersByPartialEmail(partialEmail) no implementat.");

        /*
         *    AQUEST CODI ES BO !!!!!!
         *    
         *    NO ESBORRAR  !!!!!
         *    
        SearchStatus ss = checkMinimumPartialString(partialEmail, "partialEmail");
        if (ss != null) {
            return new SearchUsersResult(ss);
        }
        
        // https://intranet.caib.es/soffid/webservice/scim2/v1/User?filter=userName co "u885"
        
        final String urlOperation = "/User?filter='attributes." + Attributes.E_MAIL_CONTACTE + "' co \"" + partialEmail + "\""; 
        
        SoffidUserResults results = callToURL(urlOperation, SoffidUserResults.class);
        
        final int maxAllowed = getMaxAllowedNumberOfResults();
        
        if (results.getTotalResults() > maxAllowed) {
            SearchStatus smax = errorMassaResultats(maxAllowed, results.getTotalResults());
            return new SearchUsersResult(smax);
        }
        
        List<UserInfo> us = soffidUserResultToUserInfos(results);
        
        return new SearchUsersResult(us);
        */
    }

    @Override
    public SearchUsersResult getUsersByPartialNameOrPartialSurnames(String partialNameOrSurname) throws Exception {

        //https://intranet.caib.es/soffid/webservice/scim2/v1/User?filter=firstName co "Onofre" OR lastName co "Onofre" OR middleName co "Onofre"

        SearchStatus ss = checkMinimumPartialString(partialNameOrSurname, "partialNameOrSurname");
        if (ss != null) {
            return new SearchUsersResult(ss);
        }

        // https://intranet.caib.es/soffid/webservice/scim2/v1/User?filter=userName co "u885"

        final String urlOperation = "/User?filter=firstName co \"" + partialNameOrSurname + "\" OR lastName co \""
                + partialNameOrSurname + "\" OR middleName co \"" + partialNameOrSurname + "\"";

        try {
            List<Resource> resources = consultaPaginada(urlOperation, isDebug(), true);

            List<UserInfo> users = resourcesToUserInfoList(resources);

            return new SearchUsersResult(users);

        } catch (ExceededMaximumAllowedResultsException e) {
            return new SearchUsersResult(e.getSmax());
        }

        /*
        SoffidUserResults results = callToURL(urlOperation, SoffidUserResults.class);
        
        final int maxAllowed = getMaxAllowedNumberOfResults();
        
        if (results.getTotalResults() > maxAllowed) {
            SearchStatus smax = errorMassaResultats(maxAllowed, results.getTotalResults());
            return new SearchUsersResult(smax);
        }
        
        List<UserInfo> us = soffidUserResultToUserInfos(results);
        
        return new SearchUsersResult(us);
        */

    }

    @Override
    public SearchUsersResult getUsersByPartialAdministrationID(String partialAdministratorID) throws Exception {

        SearchStatus ss = checkMinimumPartialString(partialAdministratorID, "partialAdministratorID");
        if (ss != null) {
            return new SearchUsersResult(ss);
        }

        // https://intranet.caib.es/soffid/webservice/scim2/v1/User?filter=userName co "u885"

        final String urlOperation = "/User?filter=attributes.NIF co \"" + partialAdministratorID + "\"";

        try {
            List<Resource> resources = consultaPaginada(urlOperation, isDebug(), true);

            List<UserInfo> users = resourcesToUserInfoList(resources);

            return new SearchUsersResult(users);

        } catch (ExceededMaximumAllowedResultsException e) {
            return new SearchUsersResult(e.getSmax());
        }

        /*
        SoffidUserResults results = callToURL(urlOperation, SoffidUserResults.class);
        
        
        final int maxAllowed = getMaxAllowedNumberOfResults();
        
        if (results.getTotalResults() > maxAllowed) {
            SearchStatus smax = errorMassaResultats(maxAllowed, results.getTotalResults());
            return new SearchUsersResult(smax);
        }
        
        List<UserInfo> us = soffidUserResultToUserInfos(results);
        
        return new SearchUsersResult(us);
        
        */
    }

    protected List<UserInfo> resourcesToUserInfoList(List<Resource> resources) throws ParseException {
        List<UserInfo> users = new ArrayList<UserInfo>(resources.size());
        for (Resource resource : resources) {
            users.add(resourceToUserInfo(resource));
        }
        return users;
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
    public SearchUsersResult getUsersByPartialValuesAnd(String usernamePartial, String firstNamePartial,
            String lastNamePartial, String emailPartial, String administrationIDPartial) throws Exception {

        final boolean isAnd = true;
        return getUsersByPartialValuesAndOr(usernamePartial, firstNamePartial, lastNamePartial, emailPartial,
                administrationIDPartial, isAnd);
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
    public SearchUsersResult getUsersByPartialValuesOr(String usernamePartial, String firstNamePartial,
            String lastNamePartial, String emailPartial, String administrationIDPartial) throws Exception {

        final boolean isAnd = false;
        return getUsersByPartialValuesAndOr(usernamePartial, firstNamePartial, lastNamePartial, emailPartial,
                administrationIDPartial, isAnd);

    }

    /*
    protected SearchUsersResult getUsersByPartialValuesAndOr(String usernamePartial, String firstNamePartial,
            String lastNamePartial, String emailPartial, String administrationIDPartial, boolean isAnd)
            throws Exception {
    
        final String metode = (isAnd) ? "getUsersByPartialValuesAnd()" : "getUsersByPartialValuesOr()";
    
        if (!empty(emailPartial) && empty(usernamePartial) && empty(firstNamePartial) && empty(lastNamePartial)
                && empty(administrationIDPartial)) {
            throw new NotImplementedException("Només ha definit el camp 'emailPartial' del mètode " + metode
                    + " però aquesta cerca no està implementada. "
                    + "Si la combina al altres camps de cerca llavors si que es pot utilitzar");
        } else {
            if (!isAnd) {
                log.warn("Ha cridat al mètode " + metode + " amb el camp 'emailPartial' definit,"
                        + " però aquest camp de cerca no està implementat,"
                        + " o sigui que la cerca actual ignorarà aquest camp");
            }
        }
    
        long startT = 0;
    
        final boolean debug = isDebug();
        if (debug) {
            startT = System.currentTimeMillis();
        }
    
        // Cercam la mitja de longitud de les cadenes de cerca.
        // Aquesta ha de superar el mínim permés
    
        // TODO NO suportam per ara EMAIL
        final String[] soffidKeys = { "userName", "firstName", "lastName",
                "attributes." + Attributes.NIF  };
    
        final String[] values = { usernamePartial, firstNamePartial, lastNamePartial,
                administrationIDPartial };
        final String[] field = { "usernamePartial", "firstNamePartial", "lastNamePartial",
                "administrationIDPartial" };
        float suma = 0;
        float count = 0;
        String camps = "";
        StringBuilder filtre = new StringBuilder();
        final int minimumCharachtersToSearch = getMinimumCharactersToSearch();
        for (int j = 0; j < values.length; j++) {
            String v = values[j];
            if (v != null && v.trim().length() != 0) {
                suma = suma + v.length();
                count = count + 1;
                if (v.length() < minimumCharachtersToSearch) {
                    camps = camps + "," + field[j];
                }
                if (filtre.length() != 0) {
                    filtre.append(isAnd ? " AND " : " OR ");
                }
                filtre.append(soffidKeys[j]).append(" co \"").append(v).append("\"");
    
                if (!isAnd && field[j].equals("lastNamePartial")) {
                    // Afegim el segon llinatge que està a "middleName"
                    filtre.append(" OR middleName co \"").append(v).append("\"");
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
    
        final String urlOperation = "/User?filter=" + filtre.toString();
    
        if (debug) {
            log.info("La recuperació de dades d'usuari en la cerca de " + metode + " es farà amb el següent filtre: "
                    + filtre.toString());
        }
    
        List<UserInfo> allResults;
        try {
            List<Resource> resources = consultaPaginada(urlOperation, isDebug(), true);
    
            allResults = resourcesToUserInfoList(resources);
    
        } catch (ExceededMaximumAllowedResultsException e) {
            return new SearchUsersResult(e.getSmax());
        }
    
    
        List<UserInfo> list;
        if (empty(emailPartial) || isAnd == false) {
            list = allResults;
        } else {
    
            // Si es AND podem aplicar filtre addicional per email per codi java
            list = new ArrayList<UserInfo>(allResults.size());
    
            for (UserInfo userInfo : allResults) {
                if (userInfo.getEmail() != null
                        && userInfo.getEmail().toLowerCase().contains(emailPartial.toLowerCase())) {
                    list.add(userInfo);
                }
            }
        }
    
        if (debug) {
            log.info("La recuperació de dades d'usuari en la cerca de " + metode + " ]" + filtre.toString()
                    + "[, ha tardat " + (System.currentTimeMillis() - startT) + "ms");
        }
    
        return new SearchUsersResult(list);
    }
    
    */

    public SearchUsersResult getUsersByPartialValuesAndOr(String usernamePartial, String firstNamePartial,
            String lastNamePartial, /* String lastNamePartial2, */ String emailPartial, String administrationIDPartial,
            boolean isAnd) throws Exception {

        final String metode = (isAnd) ? "getUsersByPartialValuesAnd()" : "getUsersByPartialValuesOr()";

        if (!empty(emailPartial) && empty(usernamePartial) && empty(firstNamePartial) && empty(lastNamePartial)
                && empty(administrationIDPartial)) {
            throw new NotImplementedException("Només ha definit el camp 'emailPartial' del mètode " + metode
                    + " però aquesta cerca no està implementada. "
                    + "Si la combina al altres camps de cerca llavors si que es pot utilitzar");
        } else {
            if (!isAnd) {
                log.warn("Ha cridat al mètode " + metode + " amb el camp 'emailPartial' definit,"
                        + " però aquest camp de cerca no està implementat,"
                        + " o sigui que la cerca actual ignorarà aquest camp");
            }
        }

        long startT = 0;

        final boolean debug = isDebug();
        if (debug) {
            startT = System.currentTimeMillis();
        }

        // Cercam la mitja de longitud de les cadenes de cerca.
        // Aquesta ha de superar el mínim permés

        // TODO NO suportam per ara EMAIL
        final String[] soffidKeys = { "userName", "firstName", "lastName", // "lastName",
                "attributes." + Attributes.NIF /* , "attributes." + Attributes.E_MAIL_CONTACTE */ };

        final String[] values = { usernamePartial, firstNamePartial, lastNamePartial, // lastNamePartial2,
                administrationIDPartial, /* emailPartial, */ };
        final String[] field = { "usernamePartial", "firstNamePartial", "lastNamePartial", // "lastNamePartial",
                "administrationIDPartial", /* "emailPartial" */ };
        float suma = 0;
        float count = 0;
        String camps = "";
        StringBuilder filtre = new StringBuilder();
        final int minimumCharachtersToSearch = getMinimumCharactersToSearch();
        for (int j = 0; j < values.length; j++) {
            String v = values[j];
            if (v != null && v.trim().length() != 0) {
                suma = suma + v.length();
                count = count + 1;
                if (v.length() < minimumCharachtersToSearch) {
                    camps = camps + "," + field[j];
                }
                if (filtre.length() != 0) {
                    filtre.append(isAnd ? " AND " : " OR ");
                }

                if (field[j].equals("lastNamePartial")) {

                    String[] parts = v.split(" ");
                    filtre.append(" ( ");
                    for (int k = 0; k < parts.length; k++) {
                        if (k != 0) {
                            filtre.append(" AND ");
                        }
                        String part = parts[k];

                        filtre.append("(").append("lastName").append(" co \"").append(part)
                                .append("\" OR middleName co \"").append(part).append("\")");

                    }
                    filtre.append(" ) ");

                } else {
                    filtre.append(soffidKeys[j]).append(" co \"").append(v).append("\"");
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

        final String urlOperation = "/User?filter=" + filtre.toString();

        if (debug) {
            log.info("La recuperació de dades d'usuari en la cerca de " + metode + " es farà amb el següent filtre: "
                    + filtre.toString());
        }

        List<UserInfo> allResults;
        try {
            List<Resource> resources = consultaPaginada(urlOperation, isDebug(), true);

            allResults = resourcesToUserInfoList(resources);

        } catch (ExceededMaximumAllowedResultsException e) {
            return new SearchUsersResult(e.getSmax());
        }

        /*
        SoffidUserResults results = callToURL(urlOperation, SoffidUserResults.class);
        
        final int maxAllowed = getMaxAllowedNumberOfResults();
        
        // Massa resultats ????
        if (results.getTotalResults() > maxAllowed) {
            SearchStatus smax = errorMassaResultats(maxAllowed, results.getTotalResults());
            return new SearchUsersResult(smax);
        }
        List<UserInfo> allResults = soffidUserResultToUserInfos(results);
        */

        List<UserInfo> list;
        if (empty(emailPartial) || isAnd == false) {
            list = allResults;
        } else {

            // Si es AND podem aplicar filtre addicional per email per codi java
            list = new ArrayList<UserInfo>(allResults.size());

            for (UserInfo userInfo : allResults) {
                if (userInfo.getEmail() != null
                        && userInfo.getEmail().toLowerCase().contains(emailPartial.toLowerCase())) {
                    list.add(userInfo);
                }
            }
        }

        if (debug) {
            log.info("La recuperació de dades d'usuari en la cerca de " + metode + " ]" + filtre.toString()
                    + "[, ha tardat " + (System.currentTimeMillis() - startT) + "ms");
        }

        return new SearchUsersResult(list);
    }

    // ... dins de la classe SoffidUserInformationPlugin

    public static class LoggingClientFilter implements ClientRequestFilter, ClientResponseFilter, WriterInterceptor {

        private final Logger log;

        // Constructor: rep el log del plugin
        public LoggingClientFilter(Logger log) {
            this.log = log;
        }

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            // Registra la petició sortint (capçaleres)
            log.info("HTTP Request => " + requestContext.getMethod() + " " + requestContext.getUri());
            log.info("HTTP Request Headers => " + requestContext.getStringHeaders());
            // El body del request es registra al mètode aroundWriteTo() (WriterInterceptor)
        }

        @Override
        public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
            // Interceptam l'escriptura del body del request per poder-lo registrar
            OutputStream originalStream = context.getOutputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            context.setOutputStream(buffer);
            try {
                context.proceed();
            } finally {
                byte[] bytes = buffer.toByteArray();
                log.info("HTTP Request Body => " + new String(bytes, "UTF-8"));
                // Tornam a escriure el body a l'stream original
                originalStream.write(bytes);
                context.setOutputStream(originalStream);
            }
        }

        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext)
                throws IOException {
            // Registra la resposta rebuda (capçaleres)
            log.info("HTTP Response <= Status: " + responseContext.getStatus());
            log.info("HTTP Response Headers <= " + responseContext.getHeaders());

            // Llegim el body de la resposta i el "rebobinam" perquè el pugui llegir readEntity()
            if (responseContext.hasEntity()) {
                InputStream is = responseContext.getEntityStream();
                byte[] bytes = readAllBytes(is);
                log.info("HTTP Response Body <= " + new String(bytes, "UTF-8"));
                // Reposam l'stream perquè no quedi consumit
                responseContext.setEntityStream(new ByteArrayInputStream(bytes));
            }
        }

        // Llegeix tot l'stream a un array de bytes (Java 8 compatible)
        private static byte[] readAllBytes(InputStream is) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] tmp = new byte[4096];
            int n;
            while ((n = is.read(tmp)) != -1) {
                baos.write(tmp, 0, n);
            }
            return baos.toByteArray();
        }
    }

}
