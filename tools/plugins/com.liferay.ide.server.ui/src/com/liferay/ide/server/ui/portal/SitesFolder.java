package com.liferay.ide.server.ui.portal;

import com.liferay.ide.portal.core.IPortalConnection;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.ui.LiferayServerUIPlugin;
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.wst.server.core.IServer;
import org.json.JSONArray;
import org.json.JSONObject;


public class SitesFolder extends RemoteFolder
{

    private Map<String, Long> classNameIds;
    private Map<Long, Long> companyGroupIds;

    public SitesFolder( ICommonContentExtensionSite site, IServer server, Map<String, Long> classNameIds, Map<Long, Long> companyGroupIds )
    {
        super( site, server, server, "Sites" ); //$NON-NLS-1$
        this.classNameIds = classNameIds;
        this.companyGroupIds = companyGroupIds;
    }

    protected Job createCacheChildrenJob()
    {
        return new Job( "Loading portal sites..." ) //$NON-NLS-1$
        {
            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                setCurrentStatus( LiferayServerUIPlugin.createInfoStatus( "Loading portal sites..." ) ); //$NON-NLS-1$

                IPortalConnection portalConnection = LiferayServerCore.getPortalConnection( getLiferayServer() );

                List<SiteFolder> siteFolders = new ArrayList<SiteFolder>();

                IStatus errorStatus = null;

                try
                {
                    JSONArray userSites = portalConnection.getUserSites();

                    for( int i = 0; i < userSites.length(); i++ )
                    {
                        JSONObject site = (JSONObject) userSites.get( i );

                        SiteFolder siteFolder = new SiteFolder( getExt(), getServer(), SitesFolder.this, classNameIds, companyGroupIds );
                        siteFolder.initFromJSON( site );

                        siteFolders.add( siteFolder );
                    }

                    setChildren( siteFolders.toArray( new SiteFolder[0] ) );
                }
                catch( Exception e )
                {
                    errorStatus = LiferayServerUIPlugin.createErrorStatus( e );
                }

                if( errorStatus != null )
                {
                    setCurrentStatus( errorStatus );
                }
                else
                {
                    setCurrentStatus( null );
                }

                UIUtil.refreshContent( getExt(), this );

                return Status.OK_STATUS;
            }
        };
    }

}
