
package org.fundaciobit.pluginsib.userinformation.soffid.beans;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
/**
 * 
 * @author anadal
 * 22 sept 2025 10:52:45
 */
@Generated("jsonschema2pojo")
public class Resource {

    private String lastName;
    private String createdByUser;
    private String mailServer;
    private String emailAddress;
    private String mailDomain;
    private Boolean multiSession;
    private String modifiedByUser;
    private Long id;
    private String homeServer;
    private String primaryGroupDescription;
    private String primaryGroup;
    private String profileServer;
    private String fullName;
    private Boolean active;
    private String userName;
    private String mailAlias;
    private String firstName;
    private String createdDate;
    private Meta meta;
    private List<String> schemas;
    private String modifiedDate;
    private String middleName;
    //private Attributes attributes;
    private Map<String, String> attributes;
    private String userType;
    private String shortName;

    private String comments;

    // ROL

    private String certificationDate;
    private String accountSystem;
    private String accountName;
    private String userGroupCode;
    private String roleId;
    private Boolean approvalPending;
    private String userFullName;
    private String bpmEnforced;
    private String userCode;
    private Boolean enabled;
    private Long accountId;
    private String informationSystemName;
    private String roleName;
    private Boolean removalPending;
    private String roleDescription;
    private String startDate;
    private String system;
    
    
    

    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public String getMailServer() {
        return mailServer;
    }

    public void setMailServer(String mailServer) {
        this.mailServer = mailServer;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getMailDomain() {
        return mailDomain;
    }

    public void setMailDomain(String mailDomain) {
        this.mailDomain = mailDomain;
    }

    public Boolean getMultiSession() {
        return multiSession;
    }

    public void setMultiSession(Boolean multiSession) {
        this.multiSession = multiSession;
    }

    public String getModifiedByUser() {
        return modifiedByUser;
    }

    public void setModifiedByUser(String modifiedByUser) {
        this.modifiedByUser = modifiedByUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHomeServer() {
        return homeServer;
    }

    public void setHomeServer(String homeServer) {
        this.homeServer = homeServer;
    }

    public String getPrimaryGroupDescription() {
        return primaryGroupDescription;
    }

    public void setPrimaryGroupDescription(String primaryGroupDescription) {
        this.primaryGroupDescription = primaryGroupDescription;
    }

    public String getPrimaryGroup() {
        return primaryGroup;
    }

    public void setPrimaryGroup(String primaryGroup) {
        this.primaryGroup = primaryGroup;
    }

    public String getProfileServer() {
        return profileServer;
    }

    public void setProfileServer(String profileServer) {
        this.profileServer = profileServer;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMailAlias() {
        return mailAlias;
    }

    public void setMailAlias(String mailAlias) {
        this.mailAlias = mailAlias;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCertificationDate() {
        return certificationDate;
    }

    public void setCertificationDate(String certificationDate) {
        this.certificationDate = certificationDate;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String getAccountSystem() {
        return accountSystem;
    }

    public void setAccountSystem(String accountSystem) {
        this.accountSystem = accountSystem;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getUserGroupCode() {
        return userGroupCode;
    }

    public void setUserGroupCode(String userGroupCode) {
        this.userGroupCode = userGroupCode;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Boolean getApprovalPending() {
        return approvalPending;
    }

    public void setApprovalPending(Boolean approvalPending) {
        this.approvalPending = approvalPending;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getBpmEnforced() {
        return bpmEnforced;
    }

    public void setBpmEnforced(String bpmEnforced) {
        this.bpmEnforced = bpmEnforced;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getInformationSystemName() {
        return informationSystemName;
    }

    public void setInformationSystemName(String informationSystemName) {
        this.informationSystemName = informationSystemName;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Boolean getRemovalPending() {
        return removalPending;
    }

    public void setRemovalPending(Boolean removalPending) {
        this.removalPending = removalPending;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String tab) {
        StringBuilder sb = new StringBuilder();
        sb.append(tab).append(Resource.class.getName()).append(' ').append('{').append('\n');
        sb.append(tab).append("\tlastName");
        sb.append('=');
        sb.append(((this.lastName == null) ? "<null>" : this.lastName));
        sb.append('\n');
        sb.append(tab).append("\tcreatedByUser");
        sb.append('=');
        sb.append(((this.createdByUser == null) ? "<null>" : this.createdByUser));
        sb.append('\n');
        sb.append(tab).append("\tmailServer");
        sb.append('=');
        sb.append(((this.mailServer == null) ? "<null>" : this.mailServer));
        sb.append('\n');
        sb.append(tab).append("\temailAddress");
        sb.append('=');
        sb.append(((this.emailAddress == null) ? "<null>" : this.emailAddress));
        sb.append('\n');
        sb.append(tab).append("\tmailDomain");
        sb.append('=');
        sb.append(((this.mailDomain == null) ? "<null>" : this.mailDomain));
        sb.append('\n');
        sb.append(tab).append("\tmultiSession");
        sb.append('=');
        sb.append(((this.multiSession == null) ? "<null>" : this.multiSession));
        sb.append('\n');
        sb.append(tab).append("\tmodifiedByUser");
        sb.append('=');
        sb.append(((this.modifiedByUser == null) ? "<null>" : this.modifiedByUser));
        sb.append('\n');
        sb.append(tab).append("\tid");
        sb.append('=');
        sb.append(((this.id == null) ? "<null>" : this.id));
        sb.append('\n');
        sb.append(tab).append("\thomeServer");
        sb.append('=');
        sb.append(((this.homeServer == null) ? "<null>" : this.homeServer));
        sb.append('\n');
        sb.append(tab).append("\tprimaryGroupDescription");
        sb.append('=');
        sb.append(((this.primaryGroupDescription == null) ? "<null>" : this.primaryGroupDescription));
        sb.append('\n');
        sb.append(tab).append("\tprimaryGroup");
        sb.append('=');
        sb.append(((this.primaryGroup == null) ? "<null>" : this.primaryGroup));
        sb.append('\n');
        sb.append(tab).append("\tprofileServer");
        sb.append('=');
        sb.append(((this.profileServer == null) ? "<null>" : this.profileServer));
        sb.append('\n');
        sb.append(tab).append("\tfullName");
        sb.append('=');
        sb.append(((this.fullName == null) ? "<null>" : this.fullName));
        sb.append('\n');
        sb.append(tab).append("\tactive");
        sb.append('=');
        sb.append(((this.active == null) ? "<null>" : this.active));
        sb.append('\n');
        sb.append(tab).append("\tuserName");
        sb.append('=');
        sb.append(((this.userName == null) ? "<null>" : this.userName));
        sb.append('\n');
        sb.append(tab).append("\tmailAlias");
        sb.append('=');
        sb.append(((this.mailAlias == null) ? "<null>" : this.mailAlias));
        sb.append('\n');
        sb.append(tab).append("\tfirstName");
        sb.append('=');
        sb.append(((this.firstName == null) ? "<null>" : this.firstName));
        sb.append('\n');
        sb.append(tab).append("\tcreatedDate");
        sb.append('=');
        sb.append(((this.createdDate == null) ? "<null>" : this.createdDate));
        sb.append('\n');
        sb.append(tab).append("\tmeta");
        sb.append('=');
        sb.append(((this.meta == null) ? "<null>\n" : this.meta.toString(tab + "\t")));

        sb.append(tab).append("\tschemas");
        sb.append('=');
        sb.append(((this.schemas == null) ? "<null>" : this.schemas));
        sb.append('\n');
        sb.append(tab).append("\tmodifiedDate");
        sb.append('=');
        sb.append(((this.modifiedDate == null) ? "<null>" : this.modifiedDate));
        sb.append('\n');
        sb.append(tab).append("\tmiddleName");
        sb.append('=');
        sb.append(((this.middleName == null) ? "<null>" : this.middleName));
        sb.append('\n');
        sb.append(tab).append("\tattributes");
        sb.append('=');
        sb.append(((this.attributes == null) ? "<null>" : this.attributes));
        sb.append('\n');
        sb.append(tab).append("\tuserType");
        sb.append('=');
        sb.append(((this.userType == null) ? "<null>" : this.userType));
        sb.append('\n');

        sb.append(tab).append("\tshortName");
        sb.append('=');
        sb.append(((this.shortName == null) ? "<null>" : this.shortName));
        sb.append('\n');

        sb.append(tab).append("\tcomments");
        sb.append('=');
        sb.append(((this.comments == null) ? "<null>" : this.comments));
        sb.append('\n');
        
        // ========== ROLES

        if ((this.certificationDate != null)) {
            sb.append(tab).append("\tcertificationDate").append('=').append(this.certificationDate).append('\n');
        }

        if ((this.accountSystem != null)) {
            sb.append(tab).append("\taccountSystem").append('=').append(this.accountSystem).append('\n');
        }

        if ((this.accountName != null)) {
            sb.append(tab).append("\taccountName").append('=').append(this.accountName).append('\n');
        }

        if ((this.userGroupCode != null)) {
            sb.append(tab).append("\tuserGroupCode").append('=').append(this.userGroupCode).append('\n');
        }

        if ((this.roleId != null)) {
            sb.append(tab).append("\troleId").append('=').append(this.roleId).append('\n');
        }

        if ((this.approvalPending != null)) {
            sb.append(tab).append("\tapprovalPending").append('=').append(this.approvalPending).append('\n');
        }

        if ((this.userFullName != null)) {
            sb.append(tab).append("\tuserFullName").append('=').append(this.userFullName).append('\n');
        }

        if ((this.bpmEnforced != null)) {
            sb.append(tab).append("\tbpmEnforced").append('=').append(this.bpmEnforced).append('\n');
        }

        if ((this.userCode != null)) {
            sb.append(tab).append("\tuserCode").append('=').append(this.userCode).append('\n');
        }

        if ((this.enabled != null)) {
            sb.append(tab).append("\tenabled").append('=').append(this.enabled).append('\n');
        }

        if ((this.accountId != null)) {
            sb.append(tab).append("\taccountId").append('=').append(this.accountId).append('\n');
        }

        if ((this.informationSystemName != null)) {
            sb.append(tab).append("\tinformationSystemName").append('=').append(this.informationSystemName)
                    .append('\n');
        }
        
        
        if ((this.roleName != null)) {
            sb.append(tab).append("\troleName").append('=').append(this.roleName).append('\n');
        }

        if ((this.removalPending != null)) {
            sb.append(tab).append("\tremovalPending").append('=').append(this.removalPending).append('\n');
        }

        if ((this.roleDescription != null)) {
            sb.append(tab).append("\troleDescription").append('=').append(this.roleDescription).append('\n');
        }

        if ((this.startDate != null)) {
            sb.append(tab).append("\tstartDate").append('=').append(this.startDate)
                    .append('\n');
        }

        if ((this.system != null)) {
            sb.append(tab).append("\tsystem").append('=').append(this.system)
                    .append('\n');
        }

        if ((this.additionalProperties != null && !this.additionalProperties.isEmpty())) {
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
