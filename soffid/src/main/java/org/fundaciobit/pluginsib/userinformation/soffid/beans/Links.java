
package org.fundaciobit.pluginsib.userinformation.soffid.beans;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Links {

    private String roleAccounts;
    private String groupUsers;
    private String accounts;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public String getRoleAccounts() {
        return roleAccounts;
    }

    public void setRoleAccounts(String roleAccounts) {
        this.roleAccounts = roleAccounts;
    }

    public String getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(String groupUsers) {
        this.groupUsers = groupUsers;
    }

    public String getAccounts() {
        return accounts;
    }

    public void setAccounts(String accounts) {
        this.accounts = accounts;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {

        return toString("");
    }

    public String toString(String tab) {
        StringBuilder sb = new StringBuilder();
        sb.append(Links.class.getName()).append(' ').append('{').append('\n');
        sb.append(tab).append("\troleAccounts");
        sb.append('=');
        sb.append(((this.roleAccounts == null) ? "<null>" : this.roleAccounts));
        sb.append('\n');
        sb.append(tab).append("\tgroupUsers");
        sb.append('=');
        sb.append(((this.groupUsers == null) ? "<null>" : this.groupUsers));
        sb.append('\n');
        sb.append(tab).append("\taccounts");
        sb.append('=');
        sb.append(((this.accounts == null) ? "<null>" : this.accounts));
        sb.append('\n');
        if ((this.additionalProperties != null) && !this.additionalProperties.isEmpty()) {
            sb.append(tab).append("\tadditionalProperties");
            sb.append('=');
            sb.append(((this.additionalProperties == null) ? "<null>" : this.additionalProperties));
            sb.append('\n');
        }

        sb.append(tab).append('}');

        sb.append('\n');
        return sb.toString();
    }
}
