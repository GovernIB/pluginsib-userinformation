package org.fundaciobit.pluginsib.userinformation;

import java.util.Date;
import java.util.Map;

/**
 * 
 * @author anadal
 * 
 */
public class UserInfo {

    public enum Gender {
        MALE, FEMALE, OTHER, UNKNOWN
    }

    /**
     * Identificador intern de cada sistema
     */
    String id;

    String username;

    String administrationID;

    String name;

    String surname1;

    String surname2;

    String email;

    String language;

    String phoneNumber;

    Gender gender = Gender.UNKNOWN;

    String address;

    String company;

    String companyArea;

    String companyDepartment;

    String website;

    Date birthDate;

    Date creationDate;

    String notes;

    Map<String, String> socialNetworks;

    Map<String, String> attributes;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAdministrationID() {
        return administrationID;
    }

    public void setAdministrationID(String administrationID) {
        this.administrationID = administrationID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname1() {
        return surname1;
    }

    public void setSurname1(String surname1) {
        this.surname1 = surname1;
    }

    public String getSurname2() {
        return surname2;
    }

    public void setSurname2(String surname2) {
        this.surname2 = surname2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyArea() {
        return companyArea;
    }

    public void setCompanyArea(String companyArea) {
        this.companyArea = companyArea;
    }

    public String getCompanyDepartment() {
        return companyDepartment;
    }

    public void setCompanyDepartment(String companyDepartment) {
        this.companyDepartment = companyDepartment;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Map<String, String> getSocialNetworks() {
        return socialNetworks;
    }

    public void setSocialNetworks(Map<String, String> socialNetworks) {
        this.socialNetworks = socialNetworks;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getFullName() {
        StringBuffer str = new StringBuffer();
        if (this.getName() != null) {
            str.append(name);
        }
        if (this.getSurname1() != null) {
            if (str.length() != 0) {
                str.append(' ');
            }
            str.append(this.getSurname1());
        }

        if (this.getSurname2() != null) {
            if (str.length() != 0) {
                str.append(' ');
            }
            str.append(this.getSurname2());
        }
        return str.toString();
    }

    @Override
    public String toString() {
        return username + " - " + administrationID + " - " + this.getFullName() + " - " + email;
    }

}
