package org.fundaciobit.pluginsib.userinformation.keycloak;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.fundaciobit.pluginsib.core.utils.AbstractPluginProperties;
import org.fundaciobit.pluginsib.userinformation.IUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
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
public class KeyCloakUserInformationPlugin extends AbstractPluginProperties implements IUserInformationPlugin {

	private static final String PLUGINSIB_USERINFORMATION_BASE_PROPERTIES = IPLUGINSIB_BASE_PROPERTIES
			+ "userinformation.";

	protected final Logger log = Logger.getLogger(getClass());

	private static final String KEYCLOAK_BASE_PROPERTY = PLUGINSIB_USERINFORMATION_BASE_PROPERTIES + "keycloak.";

	public static final String SERVER_URL_PROPERTY = KEYCLOAK_BASE_PROPERTY + "serverurl";
	public static final String REALM_PROPERTY = KEYCLOAK_BASE_PROPERTY + "realm";

	public static final String PASSWORD_SECRET_PROPERTY = KEYCLOAK_BASE_PROPERTY + "password_secret";
	public static final String CLIENT_ID_PROPERTY = KEYCLOAK_BASE_PROPERTY + "client_id";

	public static final String CLIENT_ID_FOR_USER_AUTHENTICATION_PROPERTY = KEYCLOAK_BASE_PROPERTY
			+ "client_id_for_user_autentication";

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

	public RolesResource getKeyCloakConnectionForRoles() throws Exception {
		Keycloak keycloak = getKeyCloakConnection();

		return keycloak.realm(getPropertyRequired(REALM_PROPERTY)).roles();
	}

	public UsersResource getKeyCloakConnectionForUsers() throws Exception {
		Keycloak keycloak = getKeyCloakConnection();
		UsersResource usersResource = keycloak.realm(getPropertyRequired(REALM_PROPERTY)).users();
		return usersResource;
	}

	private Keycloak getKeyCloakConnection() throws Exception {
		Keycloak keycloak = KeycloakBuilder.builder().serverUrl(getPropertyRequired(SERVER_URL_PROPERTY))
				.realm(getPropertyRequired(REALM_PROPERTY)).clientId(getPropertyRequired(CLIENT_ID_PROPERTY))
				.clientSecret(getPropertyRequired(PASSWORD_SECRET_PROPERTY))
				.grantType(OAuth2Constants.CLIENT_CREDENTIALS) // "client_credentials"
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

		keycloak.tokenManager().getAccessToken();
		return keycloak;
	}

	public Keycloak getKeyCloakConnectionUsernamePassword(String username, String password, String clientID)
			throws Exception {
		Keycloak keycloak = KeycloakBuilder.builder().serverUrl(getPropertyRequired(SERVER_URL_PROPERTY))
				.realm(getPropertyRequired(REALM_PROPERTY)).clientId(clientID)// clientId("Keycloak-admin-for-login-users")
				.password(password).username(username).grantType(OAuth2Constants.PASSWORD) // "password"
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())

				.build();

		keycloak.tokenManager().getAccessToken();
		return keycloak;
	}

	@Override
	public UserInfo getUserInfoByAdministrationID(String administrationID) throws Exception {
		// TODO Auto-generated method stub
		// Per ara no sabem com cerca un usuari a partir del NIF
		return null;
	}

	@Override
	public UserInfo getUserInfoByUserName(String username) throws Exception {

		UsersResource usersResource = getKeyCloakConnectionForUsers();

		List<UserRepresentation> users = usersResource.search(username);

		// users.get(0).get

		if (users == null || users.size() == 0) {
			return null;
		}

		UserRepresentation user = users.get(0);
		/*
		 * System.out.println("ID: " + user.getId()); System.out.println("EMAIL: " +
		 * user.getEmail()); System.out.println("NAME: " + user.getFirstName());
		 * System.out.println("LAST NAME: " + user.getLastName());
		 * 
		 * System.out.println("USERNAME: " + user.getUsername());
		 * 
		 * System.out.println("ORIGIN: " + user.getOrigin());
		 * System.out.println("ATTRIBUTES: " + user.getAttributes());
		 * 
		 * System.out.println("getFederationLink: " + user.getFederationLink());
		 * System.out.println("getFederatedIdentities: " +
		 * user.getFederatedIdentities());
		 */

		UserInfo ui = new UserInfo();
		ui.setAddress(null);
		{
			Map<String, List<String>> atr = user.getAttributes();
			if (atr != null) {
				List<String> l = atr.get("NIF");
				if (l != null && l.size() != 0) {
					ui.setAdministrationID(l.get(0));
				}
			}
		}
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

		return ui;

	}

	@Override
	public boolean authenticate(String username, String password) throws Exception {
		try {
			String clientID = getPropertyRequired(CLIENT_ID_FOR_USER_AUTHENTICATION_PROPERTY);
			getKeyCloakConnectionUsernamePassword(username, password, clientID);
			return true;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
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
}
