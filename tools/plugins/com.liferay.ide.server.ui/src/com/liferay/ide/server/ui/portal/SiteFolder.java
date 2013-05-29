package com.liferay.ide.server.ui.portal;

import com.liferay.ide.portal.core.IPortalConnection;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.wst.server.core.IServer;
import org.json.JSONException;
import org.json.JSONObject;


public class SiteFolder extends RemoteFolder
{
    private long groupId = -1;
    private Map<String, Long> classNameIds;
    private String friendlyUrl;

    public SiteFolder( ICommonContentExtensionSite ext, IServer server, Object parent, Map<String, Long> classNameIds )
    {
        super( ext, server, parent, "<undefined>" ); //$NON-NLS-1$
        this.classNameIds = classNameIds;
    }

    public void initFromJSON( JSONObject site ) throws JSONException
    {
        this.groupId = site.getLong( "groupId" ); //$NON-NLS-1$
        this.friendlyUrl = site.getString( "friendlyURL" ); //$NON-NLS-1$
        setDisplayName( site.getString( "name" ) ); //$NON-NLS-1$
    }

    @Override
    protected Job createCacheChildrenJob()
    {
        return new Job( "Loading apps..." ) //$NON-NLS-1$
        {
            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                long ddlClassNameId = classNameIds.get( IPortalConnection.DDM_CLASSNAME );

                Object[] children =
                {
                    new AppTypeFolder( getExt(), getServer(), SiteFolder.this, "ADT", -1 ), //$NON-NLS-1$
                    new AppTypeFolder( getExt(), getServer(), SiteFolder.this, "DDL", ddlClassNameId ), //$NON-NLS-1$
                    new AppTypeFolder( getExt(), getServer(), SiteFolder.this, "WCM", -1 ), //$NON-NLS-1$
                };

                setChildren( children );

                getExt().getService().update();

                return Status.OK_STATUS;
            }

        };
    }

    public long getGroupId()
    {
        return this.groupId;
    }


}
