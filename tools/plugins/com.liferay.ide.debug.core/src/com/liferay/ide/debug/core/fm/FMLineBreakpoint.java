package com.liferay.ide.debug.core.fm;

import com.liferay.ide.debug.core.ILRDebugConstants;
import com.liferay.ide.debug.core.LiferayDebugCore;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;


public class FMLineBreakpoint extends LineBreakpoint
{

    public FMLineBreakpoint()
    {
    }

    public FMLineBreakpoint( final IResource resource, final int line ) throws CoreException
    {
        IWorkspaceRunnable runnable = new IWorkspaceRunnable()
        {
            public void run( IProgressMonitor monitor ) throws CoreException
            {
                IMarker marker = resource.createMarker( LiferayDebugCore.ID_FM_BREAKPOINT_TYPE );
                marker.setAttribute( IBreakpoint.ENABLED, Boolean.TRUE );
                marker.setAttribute( IMarker.LINE_NUMBER, line );
                marker.setAttribute( IBreakpoint.ID, getModelIdentifier() );
                marker.setAttribute( IMarker.MESSAGE, "Line breakpoint: " + resource.getName() + " [line: " + line +
                    "]" );

                setMarker( marker );
            }
        };

        run( getMarkerRule( resource ), runnable );
    }

    public String getModelIdentifier()
    {
        return ILRDebugConstants.ID_FM_DEBUG_MODEL;
    }

}
