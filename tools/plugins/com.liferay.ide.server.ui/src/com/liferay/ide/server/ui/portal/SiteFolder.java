package com.liferay.ide.server.ui.portal;

import com.liferay.ide.core.remote.APIException;
import com.liferay.ide.portal.core.IPortalConnection;
import com.liferay.ide.server.core.LiferayServerCore;
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

                IPortalConnection portalConnection = LiferayServerCore.getPortalConnection( getServer() );

                try
                {
                    long ddmClassNameId = portalConnection.fetchClassNameId( IPortalConnection.DDM_CLASSNAME );

                    Object[] children =
                    {
                        new AppTypeFolder( getExt(), getServer(), SiteFolder.this, "ADT", ids, companyGroupIds ), //$NON-NLS-1$
                        new AppTypeFolder( getExt(), getServer(), SiteFolder.this, "DDL", new Long[] { ddmClassNameId }, companyGroupIds ) //$NON-NLS-1$
                        {
                            protected boolean hasStructures()
                            {
                                return false;
                            }
                        },
                    };

                    setChildren( children );

                    UIUtil.refreshContent( getExt(), this );
                }
                catch( APIException e )
                {
                    e.printStackTrace();
                }

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
