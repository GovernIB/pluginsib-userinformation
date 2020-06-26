package org.fundaciobit.pluginsib.userinformation.keycloak;

import java.util.Arrays;

import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.keycloak.admin.client.Keycloak;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    
    public static void main(String[] args) {
		new AppTest("HOLA").testApp();
	}
    
    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	
    	
    	try {
			
    		
    		Keycloak kc = new KeyCloakUserInformationPlugin().getKeyCloakConnectionUsernamePassword("u999000","u999000", "keycloak-admin2");
    		
    		
    		
    		//kc.realm("CAIB").users().count();
    		
			 System.out.println("OK");
			 
			
    		
    		
    		
    		
    		String[] users = new KeyCloakUserInformationPlugin().getUsernamesByRol("CAR_ADMIN");
			 System.out.println(Arrays.toString(users));
    		
    		
    		
    		RolesInfo ri = new KeyCloakUserInformationPlugin().getRolesByUsername("anadal");
			System.out.println( ri.getRoles());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	/*
    		UsersResource usersResource = new KeyCloakUserInformationPlugin().getKeyCloakConnectionForUsers();
    		MappingsRepresentation mr = usersResource.get("d208fbac-805a-4ca8-a610-6d67fe26da54").roles().getAll();

    		System.out.println("ROLES: " + mr.getRealmMappings());
    		*/
    		
    		

//    	    UserResource userResource = usersResource.get("d208fbac-805a-4ca8-a610-6d67fe26da54");
//    	    System.out.println(userResource.toRepresentation().getEmail());

    		/*
    		String username = "anadal";
    		List<UserRepresentation> users = usersResource.search(username);

    		UserRepresentation user = users.get(0);
    		System.out.println("ID: " + user.getId());
    		System.out.println("EMAIL: " + user.getEmail());
    		System.out.println("NAME: " + user.getFirstName());
    		System.out.println("LAST NAME: " + user.getLastName());

    		System.out.println("USERNAME: " + user.getUsername());

    		System.out.println("ORIGIN: " + user.getOrigin());
    		System.out.println("ATTRIBUTES: " + user.getAttributes());

    		System.out.println("getFederationLink: " + user.getFederationLink());
    		System.out.println("getFederatedIdentities: " + user.getFederatedIdentities());
    		
    		
    		System.out.println(" user.getApplicationRoles() => " + user.getApplicationRoles());
    		System.out.println(" user.getClientRoles() => " + user.getClientRoles());

    		System.out.println(" user.getRealmRoles() => " + user.getRealmRoles());
    		
    		System.out.println(" user.getAccess() => " + user.getAccess());
    		
    		System.out.println(" user.getCredentials() => " + user.getCredentials());
    		
    		System.out.println(" user.getGroups() => " + user.getGroups());
    		
    		*/
    		
    		
//    		List<UserRepresentation> all = usersResource.list();
//    		
//    		for (UserRepresentation ur : all) {
//    			System.out.println("USERNAME : " + ur.getUsername());
//			}

    	
    }
}
