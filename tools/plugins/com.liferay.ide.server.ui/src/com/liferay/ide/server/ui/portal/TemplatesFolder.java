package com.liferay.ide.server.ui.portal;

import com.liferay.ide.portal.core.IPortalConnection;
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


public class TemplatesFolder extends RemoteFolder
{

    private Map<Long, Long> companyGroupIds;

    public TemplatesFolder( ICommonContentExtensionSite ext, IServer server, Object parent, Map<Long, Long> companyGroupIds )
    {
        super( ext, server, parent, "Templates" ); //$NON-NLS-1$
        this.companyGroupIds = companyGroupIds;
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
                    Long[] classNameIds = getCastedParent().getClassNameIds();

                    for( long classNameId : classNameIds )
                    {
                        JSONArray templates = connection.getTemplates( getCastedParent().getGroupId(), classNameId );

                        for( int i = 0; i < templates.length(); i++ )
                        {
                            JSONObject template = templates.getJSONObject( i );

                            TemplateEntry templateEntry = new TemplateEntry( TemplatesFolder.this );
                            templateEntry.setADT( true ); // TODO not always true
                            long companyId = template.getLong( "companyId" ); //$NON-NLS-1$
                            template.put( "companyGroupId", companyGroupIds.get( companyId ) ); //$NON-NLS-1$
                            templateEntry.initFromJSON( template );

                            templateEntries.add( templateEntry );
                        }
                    }

                    setChildren( templateEntries.toArray( new Object[0] ) );

                    UIUtil.refreshContent( getExt(), this );
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
