package com.liferay.ide.server.ui.portal;

import com.liferay.ide.portal.core.IPortalConnection;
import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.wst.server.core.IServer;


public abstract class RemoteFolder extends Node
{
    private ICommonContentExtensionSite ext;
    private IServer server;
    private IStatus currentStatus;
    private Object[] cachedChildren;
    private Job cacheChildrenJob;

    public RemoteFolder( ICommonContentExtensionSite ext, IServer server, Object parent, String displayName )
    {
        super( parent, displayName );
        this.ext = ext;
        this.server = server;

        LiferayServerCore.updatePortalConnectionSettings( getLiferayServer() );
    }

    public IPortalConnection getPortalConnection()
    {
        return LiferayServerCore.getPortalConnection( getLiferayServer() );
    }

    private void scheduleCacheChildrenJob()
    {
        if( cacheChildrenJob == null )
        {
            cacheChildrenJob = createCacheChildrenJob();

            if( cacheChildrenJob != null )
            {
                cacheChildrenJob.schedule();
            }
        }

    }

    protected void refresh ()
    {
        UIUtil.refreshContent( getExt(), this );
    }

    protected abstract Job createCacheChildrenJob();

    public Object[] getChildren()
    {
        if( getServer().getServerState() != IServer.STATE_STARTED )
        {
            return null;
        }

        if( getLiferayServer() == null )
        {
            return null;
        }

        if( this.cachedChildren == null )
        {
            scheduleCacheChildrenJob();

            return new Object[] { createLoadingNode() };
        }

        return this.cachedChildren;
    }

    protected void setChildren( Object[] children )
    {
        this.cachedChildren = children;
    }

    protected ILiferayServer getLiferayServer()
    {
        return (ILiferayServer) getServer().loadAdapter( ILiferayServer.class, null );
    }

    public IStatus getStatus()
    {
        return this.currentStatus;
    }

    public void setCurrentStatus( IStatus status )
    {
        this.currentStatus = status;
    }

    public IServer getServer()
    {
        return this.server;
    }

    public ICommonContentExtensionSite getExt()
    {
        return this.ext;
    }

    protected Object createLoadingNode()
    {
        return new LoadingNode( this, "Loading..." ); //$NON-NLS-1$
    }

    public void clearCache()
    {
        this.cachedChildren = null;
        this.cacheChildrenJob = null;
    }
}
