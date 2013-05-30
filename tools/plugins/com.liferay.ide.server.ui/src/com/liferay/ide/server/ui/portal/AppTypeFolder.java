package com.liferay.ide.server.ui.portal;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.ui.util.UIUtil;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.wst.server.core.IServer;


public class AppTypeFolder extends RemoteFolder
{

    private long classNameId;
    private Map<Long, Long> companyGroupIds;

    public AppTypeFolder( ICommonContentExtensionSite ext,
                          IServer server,
                          Object parent,
                          String displayName,
                          long classNameId, Map<Long, Long> companyGroupIds )
    {
        super( ext, server, parent, displayName );

        this.classNameId = classNameId;
        this.companyGroupIds = companyGroupIds;
    }

    @Override
    protected Job createCacheChildrenJob()
    {
        return new Job( StringPool.EMPTY )
        {
            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                Object[] children =
                {
                    new StructuresFolder( getExt(), getServer(), AppTypeFolder.this ),
                    new TemplatesFolder( getExt(), getServer(), AppTypeFolder.this, companyGroupIds ),
                };

                setChildren( children );

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

    public long getClassNameId()
    {
        return this.classNameId;
    }

}