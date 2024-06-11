package org.fundaciobit.pluginsib.userinformation;

/**
 * 
 * @author anadal
 * 
 */
public class RolesInfo {

    protected String username;

    protected String[] roles;

    public RolesInfo() {
    }

    /**
     * @param username
     * @param roles
     */
    public RolesInfo(String username, String[] roles) {
        super();
        this.username = username;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

}
