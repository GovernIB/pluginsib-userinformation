package org.fundaciobit.pluginsib.userinformation.keycloak;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.UserInfo;

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
			
    		String basepackage = "org.fundaciobit.sample.";
    		
    		Properties prop = new Properties();
    		
    		prop.load(new FileInputStream(new File("sample.properties")));
    		
    		KeyCloakUserInformationPlugin kcui =new KeyCloakUserInformationPlugin(basepackage, prop);
    		
    		
    		String[] usernames = {"carpeta", "admin", "anadal"};
    		for (int i = 0; i < usernames.length; i++) {
    			UserInfo ui = kcui.getUserInfoByUserName(usernames[i]);
        		
        		System.out.println(" - " + ui.getName() + " " + ui.getSurname1());
			}
    		
			 
    		
    		//String[] users = kcui.getUsernamesByRol("CAR_ADMIN");
			//System.out.println(Arrays.toString(users));
    		
    		
    		
    		RolesInfo ri = kcui.getRolesByUsername("anadal");
			System.out.println(Arrays.toString( ri.getRoles()));
			
			System.out.println("Authenticate: " + kcui.authenticate("u999000", "u999000"));
			System.out.println("Authenticate: " + kcui.authenticate("u999000", "u999000b"));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
}
