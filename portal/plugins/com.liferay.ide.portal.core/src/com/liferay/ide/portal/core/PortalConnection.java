/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portal.core;

import com.liferay.ide.core.remote.APIException;
import com.liferay.ide.core.remote.RemoteConnection;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 */
public class PortalConnection extends RemoteConnection implements IPortalConnection
{
    private static final String _CLASSNAME = "/classname";  //$NON-NLS-1$
    private static final String _COMPANY = "/company"; //$NON-NLS-1$
    private static final String _GROUP = "/group"; //$NON-NLS-1$
//    private static final String _JOURNALARTICLE = "/journalarticle"; //$NON-NLS-1$
    private static final String _JOURNALSTRUCTURE = "/journalstructure"; //$NON-NLS-1$
    private static final String _JOURNALTEMPLATE = "/journaltemplate"; //$NON-NLS-1$
    private static final String _USER = "/user"; //$NON-NLS-1$
    private static final String _DDMTEMPLATE = "/ddmtemplate"; //$NON-NLS-1$

    private static final String FETCH_CLASSNAME_ID_API = _API + _CLASSNAME + "/fetch-class-name-id"; //$NON-NLS-1$
//    private static final String GET_ARTICLES_BY_USER_ID_API = _API + _JOURNALARTICLE + "/get-articles-by-user-id"; //$NON-NLS-1$
    private static final String GET_COMPANY_BY_VIRTUAL_HOST_API = _API + _COMPANY + "/get-company-by-virtual-host"; //$NON-NLS-1$
//    private static final String GET_JOURNAL_ARTICLES_API = _API + _JOURNALARTICLE + "/get-articles"; //$NON-NLS-1$
    private static final String GET_STRUCTURE_TEMPLATES_API = _API + _JOURNALTEMPLATE + "/get-structure-templates"; //$NON-NLS-1$
    private static final String GET_STRUCTURES_API = _API + _JOURNALSTRUCTURE + "/get-structures"; //$NON-NLS-1$
    private static final String GET_USER_BY_EMAIL_ADDRESS_API = _API + _USER + "/get-user-by-email-address"; //$NON-NLS-1$
    private static final String GET_USER_SITES_API = _API + _GROUP + "/get-user-sites"; //$NON-NLS-1$
    private static final String GET_TEMPLATES_API = _API + _DDMTEMPLATE + "/get-templates";  //$NON-NLS-1$

    public JSONObject getCompanyIdByVirtualHost() throws APIException
    {
        JSONObject company = null;

        Object jsonResponse = getJSONAPI( GET_COMPANY_BY_VIRTUAL_HOST_API, "virtualHost", getHost() ); //$NON-NLS-1$

        if( jsonResponse instanceof JSONObject )
        {
            company = (JSONObject) jsonResponse;
        }
        else
        {
            throw new APIException( GET_COMPANY_BY_VIRTUAL_HOST_API, "Unable to get JSONObject" ); //$NON-NLS-1$
        }

        return company;
    }

//    public JSONArray getJournalArticles( long groupId, long folderId ) throws APIException
//    {
//        JSONArray journalArticles = null;
//
//        Object jsonResponse = getJSONAPI( GET_JOURNAL_ARTICLES_API, "groupId", groupId, "folderId", folderId ); //$NON-NLS-1$ //$NON-NLS-2$
//
//        if( jsonResponse instanceof JSONArray )
//        {
//            journalArticles = (JSONArray) jsonResponse;
//        }
//        else
//        {
//            throw new APIException( GET_JOURNAL_ARTICLES_API, "Unable to get JSONArray" ); //$NON-NLS-1$
//        }
//
//        return journalArticles;
//    }

    public JSONArray getStructures( long groupId ) throws APIException
    {
        JSONArray structures = null;

        Object jsonResponse = getJSONAPI( GET_STRUCTURES_API, "groupId", groupId ); //$NON-NLS-1$

        if( jsonResponse instanceof JSONArray )
        {
            structures = (JSONArray) jsonResponse;
        }
        else
        {
            throw new APIException( GET_STRUCTURES_API, "Unable to get JSONArray" ); //$NON-NLS-1$
        }

        return structures;
    }

    public JSONArray getStructureTemplates( long groupId, long structureId ) throws APIException
    {
        JSONArray structureTemplates = null;

        Object jsonResponse = getJSONAPI( GET_STRUCTURE_TEMPLATES_API, "groupId", groupId, "structureId", structureId ); //$NON-NLS-1$ //$NON-NLS-2$

        if( jsonResponse instanceof JSONArray )
        {
            structureTemplates = (JSONArray) jsonResponse;
        }
        else
        {
            throw new APIException( GET_STRUCTURE_TEMPLATES_API, "Unable to get JSONArray" ); //$NON-NLS-1$
        }

        return structureTemplates;
    }

    public JSONObject getUserByEmailAddress(long companyId) throws APIException
    {
        JSONObject user = null;

        Object jsonResponse = getJSONAPI( GET_USER_BY_EMAIL_ADDRESS_API, "companyId", Long.toString( companyId), "emailAddress", getUsername() ); //$NON-NLS-1$ //$NON-NLS-2$

        if( jsonResponse instanceof JSONObject )
        {
            user = (JSONObject) jsonResponse;
        }
        else
        {
            throw new APIException( GET_USER_BY_EMAIL_ADDRESS_API, "Unable to get JSONObject" ); //$NON-NLS-1$
        }

        return user;
    }

    public JSONArray getUserSites() throws APIException
    {
        JSONArray sites = null;

        Object jsonResponse = getJSONAPI( GET_USER_SITES_API );

        if( jsonResponse instanceof JSONArray )
        {
            sites = (JSONArray) jsonResponse;
        }
        else
        {
            throw new APIException( GET_USER_SITES_API, "Unable to get JSONArray" ); //$NON-NLS-1$
        }

        return sites;
    }

    public JSONArray getTemplates( long groupId, long classNameId ) throws APIException
    {
        JSONArray templates = null;

        Object jsonResponse = getJSONAPI( GET_TEMPLATES_API,
                                          "groupId", groupId,  //$NON-NLS-1$
                                          "classNameId", classNameId ); //$NON-NLS-1$

        if( jsonResponse instanceof JSONArray )
        {
            templates = (JSONArray) jsonResponse;
        }
        else
        {
            throw new APIException( GET_TEMPLATES_API, "Unable to get JSONArray" ); //$NON-NLS-1$
        }

        return templates;
    }

    public long fetchClassNameId( String value ) throws APIException
    {
        long retval = -1;

        Object jsonResponse = getJSONAPI( FETCH_CLASSNAME_ID_API, "value", value ); //$NON-NLS-1$

        if( jsonResponse instanceof Long )
        {
            retval = ( ( Long) jsonResponse ).longValue();
        }

        return retval;
    }

}
