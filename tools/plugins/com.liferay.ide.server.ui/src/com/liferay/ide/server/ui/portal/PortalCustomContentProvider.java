package com.liferay.ide.server.ui.portal;

import com.liferay.ide.portal.core.IPortalConnection;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.ui.LiferayServerUIPlugin;
import com.liferay.ide.server.ui.PluginsCustomContentProvider;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;
import org.eclipse.wst.server.core.IServer;


public class PortalCustomContentProvider extends PluginsCustomContentProvider
{

    private final Map<String, SitesFolder> sitesFolders = new HashMap<String, SitesFolder>();
    private final Map<String, Long> appTypeClassNameIds = new HashMap<String, Long>();

    private final Map<String, IStatus> checkApiStatuses = new HashMap<String, IStatus>();

    public PortalCustomContentProvider()
    {
        super();
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    public Object[] getChildren( Object parentElement )
    {
        if( parentElement instanceof IServer )
        {
            return null;
        }
        else if( parentElement instanceof SitesFolder )
        {
            return ( (SitesFolder) parentElement ).getChildren();
        }

        return null;
    }

    public Object getParent( Object element )
    {
        if( element instanceof IWorkspaceRoot )
        {
            return null;
        }

        return null;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public void getPipelinedChildren( final Object parent, final Set currentChildren )
    {
        if( parent instanceof IServer )
        {
            final IServer server = (IServer) parent;

            if( server.getServerState() == IServer.STATE_STARTED )
            {
                final SitesFolder sitesNode = this.sitesFolders.get( server.getId() );

                if( sitesNode == null )
                {
                    final IStatus checkApiStatus = this.checkApiStatuses.get( server.getId() );

                    if( checkApiStatus == null || ! checkApiStatus.isOK() )
                    {
                        Job checkJob = new Job( "Checking for Portal API..." ) //$NON-NLS-1$
                        {
                            @Override
                            protected IStatus run( IProgressMonitor monitor )
                            {
                                try
                                {
                                    IPortalConnection portalConnection = LiferayServerCore.getPortalConnection( server );

                                    long ddlClassNameId = portalConnection.fetchClassNameId( IPortalConnection.DDM_CLASSNAME );

                                    appTypeClassNameIds.put( IPortalConnection.DDM_CLASSNAME, ddlClassNameId );

                                    PortalCustomContentProvider.this.checkApiStatuses.put(
                                        server.getId(), Status.OK_STATUS );

                                    UIUtil.refreshContent( getConfig(), server );
                                }
                                catch (Exception e)
                                {
                                   PortalCustomContentProvider.this.checkApiStatuses.put(
                                        server.getId(), LiferayServerUIPlugin.createErrorStatus( e ) );
                                }

                                return Status.OK_STATUS;
                            }
                        };

                        // for now mark this as Info so a check job isn't created again
                        PortalCustomContentProvider.this.checkApiStatuses.put(
                            server.getId(), LiferayServerUIPlugin.createInfoStatus( "checking api" ) ); //$NON-NLS-1$

                        checkJob.schedule();
                    }
                    else
                    {
                        if( checkApiStatus.isOK() )
                        {
                            insertSitesNode( server, currentChildren );
                            PortalCustomContentProvider.this.checkApiStatuses.put( server.getId(), null );
                        }
                        else
                        {
                            LiferayServerUIPlugin.logInfo("Portal API unavailable.", checkApiStatus); //$NON-NLS-1$
                        }
                    }
                }
                else
                {
                    if( !currentChildren.contains( sitesNode ) )
                    {
                        currentChildren.add( sitesNode );
                        sitesNode.getChildren(); // make sure children are cached.
                    }
                }
            }
            else
            {
                this.sitesFolders.put( server.getId(), null );
                this.checkApiStatuses.put( server.getId(), null );
            }
        }
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private void insertSitesNode( IServer server, Set currentChildren )
    {
        //put sites folder at the top of the list
        SitesFolder node = new SitesFolder( this.getConfig(), server, this.appTypeClassNameIds );

        this.sitesFolders.put( server.getId(), node );

        currentChildren.add( node );
    }

    public Object getPipelinedParent( final Object object, final Object suggestedParent )
    {
        if( object instanceof SitesFolder )
        {
            return ( (SitesFolder) object ).getParent();

            // if ( ProjectUtil.isLiferayProject( project ) && this.sitesContentNode != null )
            // {
            // return this.sitesContentNode;
            // }
        }
        // else if ( anObject instanceof DefinitionsContainer && anObject.equals( this.sitesContentNode ) )
        // {
        // return this.sitesContentNode.getParent();
        // }

        return null;
    }

    public boolean hasChildren( Object element )
    {
        if( element instanceof IServer )
        {
            IServer server = (IServer) element;

            final Node sitesNode = this.sitesFolders.get( server.getId() );

            if( sitesNode != null )
            {
                return true;
            }

            if( ServerUtil.isLiferayRuntime( server ) )
            {
                if( server.getServerState() == IServer.STATE_STARTED )
                {
                    return true;
                }
            }
        }
        else if( element instanceof SitesFolder )
        {
            return true;
        }

        return false;
    }

    public boolean interceptRefresh( PipelinedViewerUpdate viewerUpdate )
    {
        for( Object refreshTarget : viewerUpdate.getRefreshTargets() )
        {
            if( refreshTarget instanceof IServer )
            {
                clearStatuses((IServer) refreshTarget);
            }
        }

        return false;
    }

    private void clearStatuses( IServer server )
    {
        if( ServerUtil.isLiferayRuntime( server ) )
        {
            if( server.getServerState() != IServer.STATE_STARTED )
            {
                this.checkApiStatuses.put( server.getId(), null );
            }
        }
    }

    public boolean interceptUpdate( PipelinedViewerUpdate viewerUpdate )
    {
        for( Object updateTarget : viewerUpdate.getRefreshTargets() )
        {
            if( updateTarget instanceof IServer )
            {
                clearStatuses( (IServer) updateTarget );
            }
        }

        return false;
    }

}
