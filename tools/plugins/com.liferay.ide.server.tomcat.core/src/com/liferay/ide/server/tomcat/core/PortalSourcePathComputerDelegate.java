package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.jst.server.tomcat.core.internal.TomcatSourcePathComputerDelegate;


@SuppressWarnings( "restriction" )
public class PortalSourcePathComputerDelegate extends TomcatSourcePathComputerDelegate
{

    @Override
    public ISourceContainer[] computeSourceContainers( ILaunchConfiguration configuration, IProgressMonitor monitor )
        throws CoreException
    {
        ISourceContainer[] containers = super.computeSourceContainers( configuration, monitor );

        IFolder diffs = CoreUtil.getProject( "fmtest-theme" ).getFolder( "docroot/_diffs/" ); //$NON-NLS-1$ //$NON-NLS-2$
        ISourceContainer[] retval = new ISourceContainer[ containers.length + 1 ];
        retval[0] = new FolderSourceContainer( diffs, true );
        System.arraycopy( containers, 0, retval, 1, containers.length );

        return retval;
    }

}
