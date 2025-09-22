package org.fundaciobit.pluginsib.userinformation.soffid;

import org.fundaciobit.pluginsib.userinformation.SearchStatus;

/**
 * Exceeded maximum allowed results exception
 * @author anadal
 * 19 sept 2025 13:09:48
 */
public class ExceededMaximumAllowedResultsException extends Exception {

    protected final SearchStatus smax;

    public ExceededMaximumAllowedResultsException(SearchStatus smax) {
        this.smax = smax;
    }

    public SearchStatus getSmax() {
        return smax;
    }

}
