package org.fundaciobit.pluginsib.userinformation.keycloakcaib;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.fundaciobit.pluginsib.core.utils.AbstractPluginProperties;
import org.fundaciobit.pluginsib.userinformation.IUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.UserInfo.Gender;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 
 * @author anadal
 *
 */
@Controller
public class KeyCloakCaibUserInformationPlugin extends AbstractPluginProperties implements IUserInformationPlugin {

    private static final String PLUGINSIB_USERINFORMATION_BASE_PROPERTIES = IPLUGINSIB_BASE_PROPERTIES
            + "userinformation.";

    protected final Logger log = Logger.getLogger(getClass());

    private static final String KEYCLOAKCAIB_BASE_PROPERTY = PLUGINSIB_USERINFORMATION_BASE_PROPERTIES
            + "keycloakcaib.";

    public static final String DEBUG_PROPERTY = KEYCLOAKCAIB_BASE_PROPERTY + "debug";

    public static final String MAPPING_PROPERTY = KEYCLOAKCAIB_BASE_PROPERTY + "mapping.";

    /**
     * 
     */
    public KeyCloakCaibUserInformationPlugin() {
        super();
    }

    /**
     * @param propertyKeyBase
     */
    public KeyCloakCaibUserInformationPlugin(String propertyKeyBase) {
        super(propertyKeyBase);
    }

    /**
     * @param propertyKeyBase
     * @param properties
     */
    public KeyCloakCaibUserInformationPlugin(String propertyKeyBase, Properties properties) {
        super(propertyKeyBase, properties);
    }

    protected boolean isDebug() {
        String debug = getProperty(DEBUG_PROPERTY, "false");
        return "true".equals(debug);
    }

    @Override
    public UserInfo getUserInfoByAdministrationID(String administrationID) throws Exception {
        // XYZ ZZZ Mirar si username és el mateix que l'usuari del principal llavors
        // retornarà informació extreta de KeyCloakPrincipal
        throw new Exception("Operació pendent d'implementacio");
    }

    @Override
    public UserInfo getUserInfoByUserName(String username) throws Exception {
        // Mirar si username és el mateix que l'usuari del principal llavors retornarà
        // informació extreta de KeyCloakPrincipal

        // PLUGIN DE CAIB !!!!!

        log.info("\n\n XYZ ZZZ  ================ ENTRA PLUGIN CAIB KEYCLOAK ============ \n\n");
        UserInfo ui = null;
        try {
            log.info(" -- 1 ---");
            ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            ui = getUserInfoByUserName(username, sra);
        } finally {
            log.info("\n\n XYZ ZZZ  ----------------- SURT PLUGIN CAIB KEYCLOAK (" + ui + ") ---------------- \n\n");
        }

        return ui;

    }

    public UserInfo getUserInfoByUserName(String username, ServletRequestAttributes sra) throws Exception {
        UserInfo ui = null;
        if (sra == null || sra.getRequest() == null) {
            String msg = "ServletRequestAttributes o HttpServletRequest val null ";
            log.error(msg);
            throw new Exception(msg);
        } else {

            log.info(" -- 1 ---");
            HttpServletRequest request = sra.getRequest();

            // https://logindes.caib.es/auth

            log.info(" -- 2 ---");
            RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request
                    .getAttribute(KeycloakSecurityContext.class.getName());

            log.info(" -- 3 ---");
            if (context == null) {
                String msg = "RefreshableKeycloakSecurityContext val null ";
                log.error(msg);
                throw new Exception(msg);
            } else {
                log.info(" -- 4 ---");
                AccessToken token = context.getToken();

                try {
                    ui = userRepresentationToUserInfo(token);

                    log.info(" -- 5 ---");

                } catch (Exception e) {

                    String msg = "Error transformant informació de KeyCloak a UserINFO o de userInfo a UsuariJPA ";
                    log.error(msg);
                    throw new Exception(msg);
                }

            }

        }
        return ui;
    }

    protected UserInfo userRepresentationToUserInfo(AccessToken token) throws Exception {

        // XYZ ZZZ
        // String MAPPING_PROPERTY = "pluginsib.userinformation.keycloak.mapping.";

        // AccessToken token = context.getToken();

        final boolean debug = isDebug();

        // final boolean debug = true;

        if (debug) {
            log.error("subject = " + token.getSubject());
            log.error("username = " + token.getPreferredUsername());
            log.error("email = " + token.getEmail());
            log.error("familyName = " + token.getFamilyName());
            log.error("givenName = " + token.getGivenName());
            log.error("realmAccess.roles = " + token.getRealmAccess().getRoles());
            log.error("scope = " + token.getScope());

            log.error("--------------------------------");
            log.error("resourceAccessRoles:");

            // XYZ ZZZ MAPPING

            Map<String, AccessToken.Access> resourceAccess = token.getResourceAccess();
            for (String key : resourceAccess.keySet()) {
                log.error(key + " = " + resourceAccess.get(key).getRoles());
            }

            log.error("--------------------------------");
            log.error("otherClaims:");
            Map<String, Object> otherClaims = token.getOtherClaims();
            for (String key : otherClaims.keySet()) {
                log.error(key + " = " + otherClaims.get(key));
            }

        }

        UserInfo ui = new UserInfo();
        ui.setAddress(null);
        ui.setCompany(null);
        ui.setEmail(token.getEmail());
        ui.setGender(null);
        ui.setLanguage(null);
        ui.setName(token.getGivenName());
        ui.setSurname1(token.getFamilyName());
        ui.setSurname2(null);
        ui.setPhoneNumber(null);
        ui.setUsername(token.getPreferredUsername());
        ui.setWebsite(null);

        {
            final Set<String> mappingsAvailable = new HashSet<String>(
                    Arrays.asList("username", "administrationID", "name", "surname1", "surname2", "email", "language",
                            "phoneNumber", "password", "gender", "address", "company", "website"));

            Map<String, Object> userAttributes = token.getOtherClaims();
            if (userAttributes != null) {

                if (debug) {
                    for (String key : userAttributes.keySet()) {
                        String value = (String) userAttributes.get(key);
                        log.info(" Attributes[" + key + "] => " + value);
                    }
                }

                for (String userInfoField : mappingsAvailable) {
                    String attributeUser = getProperty(MAPPING_PROPERTY + userInfoField);
                    if (attributeUser == null || attributeUser.trim().length() == 0) {
                        continue;
                    }

                    String attributeUserValue = (String) userAttributes.get(attributeUser);

                    if (debug) {
                        log.info(" Posant al camp " + userInfoField + " el valor " + attributeUserValue);
                    }

                    java.lang.reflect.Field field = ui.getClass().getDeclaredField(userInfoField);
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
                            log.error(" Error prosessnat mapping de GENDER (-1, 0 o 1): " + attributeUserValue);
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
        // Mirar si username és el mateix que l'usuari del principal llavors retornarà
        // true
        return false;
    }

    @Override
    public boolean authenticate(X509Certificate certificate) throws Exception {
        throw new Exception("Operació no Suportada");
    }

    @Override
    public String[] getAllUsernames() throws Exception {

        throw new Exception("Operació no Suportada");
    }

    @Override
    public RolesInfo getRolesByUsername(String username) throws Exception {
        // XYZ ZZZ Si l'username és el de principal llavors retornar els roles
        throw new Exception("Operació pendent d'implementacio");
    }

    @Override
    public String[] getUsernamesByRol(String rol) throws Exception {
        throw new Exception("Operació no Suportada");
    }
}
