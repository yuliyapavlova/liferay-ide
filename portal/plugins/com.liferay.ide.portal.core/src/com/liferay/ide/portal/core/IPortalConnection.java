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
import com.liferay.ide.core.remote.IRemoteConnection;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Gregory Amerson
 */
public interface IPortalConnection extends IRemoteConnection
{

    public static final String[] ADT_CLASS_NAMES =
    {
        "com.liferay.portlet.blogs.model.BlogsEntry", //$NON-NLS-1$
        "com.liferay.portlet.wiki.model.WikiPage",//$NON-NLS-1$
        "com.liferay.portlet.asset.model.AssetCategory",//$NON-NLS-1$
        "com.liferay.portal.kernel.repository.model.FileEntry",//$NON-NLS-1$
        "com.liferay.portlet.asset.model.AssetEntry",//$NON-NLS-1$
        "com.liferay.portal.model.LayoutSet",//$NON-NLS-1$
        "com.liferay.portlet.asset.model.AssetTag",//$NON-NLS-1$
    };

    String DDM_CLASSNAME = "com.liferay.portlet.dynamicdatamapping.model.DDMStructure"; //$NON-NLS-1$

    JSONObject getCompanyIdByVirtualHost() throws APIException;

//    JSONArray getJournalArticles( long groupId, long userId ) throws APIException;

    JSONArray getStructures( long groupId ) throws APIException;

    JSONArray getStructureTemplates( long groupId, long structureId ) throws APIException;

    JSONArray getTemplates( long groupId, long classNameId ) throws APIException;

    JSONObject getUserByEmailAddress( long companyId ) throws APIException;

    JSONArray getUserSites() throws APIException;

    long fetchClassNameId( String value ) throws APIException;

    JSONObject getGroup( long companyId, String name ) throws APIException;

}
