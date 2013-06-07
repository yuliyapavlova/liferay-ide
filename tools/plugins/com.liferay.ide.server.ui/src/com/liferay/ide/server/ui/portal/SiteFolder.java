package com.liferay.ide.server.ui.portal;

import com.liferay.ide.ui.util.UIUtil;

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
    private Map<Long, Long> companyGroupIds;

    public SiteFolder( ICommonContentExtensionSite ext,
                       IServer server,
                       Object parent,
                       Map<String, Long> classNameIds,
                       Map<Long, Long> companyGroupIds )
    {
        super( ext, server, parent, "<undefined>" ); //$NON-NLS-1$
        this.classNameIds = classNameIds;
        this.companyGroupIds = companyGroupIds;
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
                Long[] ids = classNameIds.values().toArray( new Long[0] );

                Object[] children =
                {
                    new AppTypeFolder( getExt(), getServer(), SiteFolder.this, "ADT", ids, companyGroupIds ), //$NON-NLS-1$
                };

                setChildren( children );

                UIUtil.refreshContent( getExt(), this );

                return Status.OK_STATUS;
            }

        };
    }

    public long getGroupId()
    {
        return this.groupId;
    }

    public String getFriendlyUrl()
    {
        return this.friendlyUrl;
    }

}
