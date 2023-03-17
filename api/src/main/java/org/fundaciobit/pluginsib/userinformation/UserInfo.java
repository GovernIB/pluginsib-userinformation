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

    String mobileNumber;

    Gender gender = Gender.UNKNOWN;

    String address;

    String company;

    String companyArea;

    String companyDepartment;

    String website;

    Date birthDate;

    Date creationDate;

    String notes;

    String dir3;

    String dir3Parent;

    String dir3Company;

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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

    public String getDir3() {
        return dir3;
    }

    public void setDir3(String dir3) {
        this.dir3 = dir3;
    }

    public String getDir3Parent() {
        return dir3Parent;
    }

    public void setDir3Parent(String dir3Parent) {
        this.dir3Parent = dir3Parent;
    }

    public String getDir3Company() {
        return dir3Company;
    }

    public void setDir3Company(String dir3Company) {
        this.dir3Company = dir3Company;
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

    public String toFullInfo(String tab) {

        StringBuilder str = new StringBuilder();

        if (id != null) {
            str.append(tab + "id: " + this.id + "\n");
        } //id
        if (username != null) {
            str.append(tab + "username: " + this.username + "\n");
        } //username
        if (administrationID != null) {
            str.append(tab + "administrationID: " + this.administrationID + "\n");
        } //administrationID
        if (name != null) {
            str.append(tab + "name: " + this.name + "\n");
        } //name
        if (surname1 != null) {
            str.append(tab + "surname1: " + this.surname1 + "\n");
        } //surname1
        if (surname2 != null) {
            str.append(tab + "surname2: " + this.surname2 + "\n");
        } //surname2
        if (email != null) {
            str.append(tab + "email: " + this.email + "\n");
        } //email
        if (language != null) {
            str.append(tab + "language: " + this.language + "\n");
        } //language
        if (phoneNumber != null) {
            str.append(tab + "phoneNumber: " + this.phoneNumber + "\n");
        } //phoneNumber
        if (address != null) {
            str.append(tab + "address: " + this.address + "\n");
        } //address
        if (company != null) {
            str.append(tab + "company: " + this.company + "\n");
        } //company
        if (companyArea != null) {
            str.append(tab + "companyArea: " + this.companyArea + "\n");
        } //companyArea
        if (companyDepartment != null) {
            str.append(tab + "companyDepartment: " + this.companyDepartment + "\n");
        } //companyDepartment
        if (website != null) {
            str.append(tab + "website: " + this.website + "\n");
        } //website
        if (notes != null) {
            str.append(tab + "notes: " + this.notes + "\n");
        } //notes
        if (dir3 != null) {
            str.append(tab + "dir3: " + this.dir3 + "\n");
        } //dir3
        if (dir3Parent != null) {
            str.append(tab + "dir3Parent: " + this.dir3Parent + "\n");
        } //dir3Parent

        //Gender gender = Gender.UNKNOWN;
        if (gender != null) {
            str.append(tab + "gender: " + this.gender + "\n");
        }

        if (birthDate != null) {
            str.append(tab + "birthDate: " + this.birthDate + "\n");
        }

        if (creationDate != null) {
            str.append(tab + "creationDate: " + this.creationDate + "\n");
        }

        if (socialNetworks != null && socialNetworks.size() != 0) {
            str.append(tab + "socialNetworks:\n");
            socialNetworks.forEach((k, v) -> str.append(tab + tab + "- " + k + " => " + v + "\n"));
        }

        if (attributes != null && attributes.size() != 0) {
            str.append(tab + "attributes:\n");
            attributes.forEach((k, v) -> str.append(tab + tab + "- " + k + " => " + v + "\n"));
        }

        return str.toString();
    }

}
