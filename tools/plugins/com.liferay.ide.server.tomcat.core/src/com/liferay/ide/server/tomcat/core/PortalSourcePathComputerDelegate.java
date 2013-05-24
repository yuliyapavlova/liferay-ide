package com.liferay.ide.server.tomcat.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.jst.server.tomcat.core.internal.TomcatSourcePathComputerDelegate;


@SuppressWarnings( "restriction" )
public class PortalSourcePathComputerDelegate extends TomcatSourcePathComputerDelegate
{

    @Override
    public ISourceContainer[] computeSourceContainers( ILaunchConfiguration configuration, IProgressMonitor monitor )
        throws CoreException
    {
        return super.computeSourceContainers( configuration, monitor );
    }


}
