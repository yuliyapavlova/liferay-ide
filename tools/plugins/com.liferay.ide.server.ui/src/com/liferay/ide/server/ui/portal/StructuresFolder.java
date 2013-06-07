package com.liferay.ide.server.ui.portal;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.wst.server.core.IServer;


public class StructuresFolder extends RemoteFolder
{

    public StructuresFolder( ICommonContentExtensionSite ext, IServer server, Object parent )
    {
        super( ext, server, parent, "Structures" ); //$NON-NLS-1$
    }

    @Override
    protected Job createCacheChildrenJob()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
