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
 * 30 sept 2026 9:30:59
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "lastName", "createdByUser", "modifiedDate", "userType", "shortName" })
@Generated("jsonschema2pojo")
public class User {

    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("createdByUser")
    private String createdByUser;
    @JsonProperty("modifiedDate")
    private String modifiedDate;
    @JsonProperty("userType")
    private String userType;
    @JsonProperty("shortName")
    private String shortName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("createdByUser")
    public String getCreatedByUser() {
        return createdByUser;
    }

    @JsonProperty("createdByUser")
    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    @JsonProperty("modifiedDate")
    public String getModifiedDate() {
        return modifiedDate;
    }

    @JsonProperty("modifiedDate")
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @JsonProperty("userType")
    public String getUserType() {
        return userType;
    }

    @JsonProperty("userType")
    public void setUserType(String userType) {
        this.userType = userType;
    }

    @JsonProperty("shortName")
    public String getShortName() {
        return shortName;
    }

    @JsonProperty("shortName")
    public void setShortName(String shortName) {
        this.shortName = shortName;
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
