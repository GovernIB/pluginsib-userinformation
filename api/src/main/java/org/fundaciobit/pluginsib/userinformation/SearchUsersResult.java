package org.fundaciobit.pluginsib.userinformation;

import java.util.List;

/**
 * 
 * @author anadal
 *
 */
public class SearchUsersResult {

    SearchStatus status;

    List<UserInfo> users;

    public SearchUsersResult(SearchStatus status) {
        super();
        this.status = status;
    }

    public SearchUsersResult(List<UserInfo> users) {
        super();
        this.status = new SearchStatus(SearchStatus.RESULT_OK);
        this.users = users;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }

    public SearchStatus getStatus() {
        return status;
    }

    public void setStatus(SearchStatus status) {
        this.status = status;
    }

}
