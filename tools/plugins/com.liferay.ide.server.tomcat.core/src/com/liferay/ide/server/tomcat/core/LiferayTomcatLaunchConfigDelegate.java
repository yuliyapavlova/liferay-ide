/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.debug.core.fm.FMDebugTarget;
import com.liferay.ide.server.core.ILiferayRuntime;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.jst.server.tomcat.core.internal.TomcatLaunchConfigurationDelegate;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.osgi.framework.Version;

@SuppressWarnings( "restriction" )
public class LiferayTomcatLaunchConfigDelegate extends TomcatLaunchConfigurationDelegate
{

    private static final String STOP_SERVER = "stop-server"; //$NON-NLS-1$
    private static final String FALSE = "false"; //$NON-NLS-1$
    private String tempMode;

    @Override
    public String getVMArguments( ILaunchConfiguration configuration ) throws CoreException
    {
        String retval = super.getVMArguments( configuration );

        String stopServer = configuration.getAttribute(STOP_SERVER, FALSE);

       if( ILaunchManager.DEBUG_MODE.equals( tempMode ) && FALSE.equals( stopServer ) )
        {
            try
            {
                final IServer server = ServerUtil.getServer( configuration );

                final ILiferayRuntime liferayRuntime =
                    (ILiferayRuntime) server.getRuntime().loadAdapter( ILiferayRuntime.class, null );

                final Version version = new Version( liferayRuntime.getPortalVersion() );

                if( CoreUtil.compareVersions( version, ILiferayConstants.V6_2_0 ) >= 0 )
                {
                    retval += " -Dfreemarker.debug.password=fmdebug -Dfreemarker.debug.port=7600"; //$NON-NLS-1$
                }
            }
            catch( CoreException e )
            {
            }
        }

        return retval;
    }

    @Override
    public void launch( ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor )
        throws CoreException
    {
        ISourceLookupDirector sourceLocator = new PortalSourceLookupDirector();
        sourceLocator.setSourcePathComputer(getLaunchManager().getSourcePathComputer("com.liferay.ide.server.tomcat.portalSourcePathComputer")); //$NON-NLS-1$
        sourceLocator.initializeDefaults(configuration);
        launch.setSourceLocator(sourceLocator);

        this.tempMode = mode;
        super.launch( configuration, mode, launch, monitor );
        this.tempMode = null;

        String stopServer = configuration.getAttribute(STOP_SERVER, FALSE);

        if( ILaunchManager.DEBUG_MODE.equals( mode ) && FALSE.equals( stopServer ) )
        {
            IDebugTarget target = new FMDebugTarget( launch, launch.getProcesses()[0] );
            launch.addDebugTarget( target );
        }
    }
}
