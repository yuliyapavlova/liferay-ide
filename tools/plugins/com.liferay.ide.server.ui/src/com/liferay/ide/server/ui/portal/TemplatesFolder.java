package com.liferay.ide.server.ui.portal;

import com.liferay.ide.portal.core.IPortalConnection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.wst.server.core.IServer;
import org.json.JSONArray;
import org.json.JSONObject;


public class TemplatesFolder extends RemoteFolder
{

    public TemplatesFolder( ICommonContentExtensionSite ext, IServer server, Object parent )
    {
        super( ext, server, parent, "Templates" ); //$NON-NLS-1$
    }

    @Override
    protected Job createCacheChildrenJob()
    {
        return new Job( "Loading templates..." ) //$NON-NLS-1$
        {
            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                try
                {
                    IPortalConnection connection = getPortalConnection();

                    List<TemplateEntry> templateEntries = new ArrayList<TemplateEntry>();

                    JSONArray templates = connection.getTemplates( getCastedParent().getGroupId(), getCastedParent().getClassNameId() );

                    for( int i = 0; i < templates.length(); i++ )
                    {
                        JSONObject template = templates.getJSONObject( i );

                        TemplateEntry templateEntry = new TemplateEntry( TemplatesFolder.this );
                        templateEntry.initFromJSON( template );

                        templateEntries.add( templateEntry );
                    }

                    setChildren( templateEntries.toArray( new Object[0] ) );

                    getExt().getService().update();
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                }

                return Status.OK_STATUS;
            }
        };
    }

    public AppTypeFolder getCastedParent()
    {
        return (AppTypeFolder) getParent();
    }

}
