package com.liferay.ide.server.ui.portal;

import com.liferay.ide.core.util.StringPool;
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


public class AppTypeFolder extends RemoteFolder
{

    private Long[] classNameIds;
    private Map<Long, Long> companyGroupIds;

    public AppTypeFolder( ICommonContentExtensionSite ext,
                          IServer server,
                          Object parent,
                          String displayName,
                          Long[] classNameIds,
                          Map<Long, Long> companyGroupIds )
    {
        super( ext, server, parent, displayName );

        this.classNameIds = classNameIds;
        this.companyGroupIds = companyGroupIds;
    }

    protected boolean hasStructures()
    {
        return true;
    }

    @Override
    protected Job createCacheChildrenJob()
    {
        return new Job( StringPool.EMPTY )
        {
            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                List<Object> children = new ArrayList<Object>();

                if( hasStructures() )
                {
                    children.add( new StructuresFolder( getExt(), getServer(), AppTypeFolder.this ) );
                }

                children.add( new TemplatesFolder( getExt(), getServer(), AppTypeFolder.this, companyGroupIds ) );

                setChildren( children.toArray( new Object[0] ) );

                UIUtil.refreshContent( getExt(), this );

                return Status.OK_STATUS;
            }
        };
    }

    public SiteFolder getCastedParent()
    {
        return (SiteFolder) getParent();
    }

    public long getGroupId()
    {
        return getCastedParent().getGroupId();
    }

    public Long[] getClassNameIds()
    {
        return this.classNameIds;
    }

}
