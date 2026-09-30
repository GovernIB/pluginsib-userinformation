package org.fundaciobit.pluginsib.userinformation.soffid.beans;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * @author anadal (u80067)
 * 30 sept 2026 9:31:11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "valid", "expired", "failureReason", "principalName", "sessionCookie", "attributes", "user",
        "identityProvider" })
@Generated("jsonschema2pojo")
public class ValidateCredentials {

    @JsonProperty("valid")
    private Boolean valid;
    @JsonProperty("expired")
    private Boolean expired;
    @JsonProperty("failureReason")
    private String failureReason;
    @JsonProperty("principalName")
    private String principalName;
    @JsonProperty("sessionCookie")
    private String sessionCookie;
    @JsonProperty("attributes")
    private Attributes attributes;
    //@JsonProperty("user")
    //private User user;

    @JsonProperty("user")
    private Resource user;

    @JsonProperty("identityProvider")
    private String identityProvider;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("valid")
    public Boolean getValid() {
        return valid;
    }

    @JsonProperty("valid")
    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    @JsonProperty("expired")
    public Boolean getExpired() {
        return expired;
    }

    @JsonProperty("expired")
    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    @JsonProperty("failureReason")
    public String getFailureReason() {
        return failureReason;
    }

    @JsonProperty("failureReason")
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @JsonProperty("principalName")
    public String getPrincipalName() {
        return principalName;
    }

    @JsonProperty("principalName")
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    @JsonProperty("sessionCookie")
    public String getSessionCookie() {
        return sessionCookie;
    }

    @JsonProperty("sessionCookie")
    public void setSessionCookie(String sessionCookie) {
        this.sessionCookie = sessionCookie;
    }

    @JsonProperty("attributes")
    public Attributes getAttributes() {
        return attributes;
    }

    @JsonProperty("attributes")
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    /*
    @JsonProperty("user")
    public User getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(User user) {
        this.user = user;
    }
    */
    
    
    @JsonProperty("user")
    public Resource getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(Resource user) {
        this.user = user;
    }

    @JsonProperty("identityProvider")
    public String getIdentityProvider() {
        return identityProvider;
    }

    @JsonProperty("identityProvider")
    public void setIdentityProvider(String identityProvider) {
        this.identityProvider = identityProvider;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
