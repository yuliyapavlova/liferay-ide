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
    
    public static final String ATTR_TEMPLATE_NAME = "templateName";

    public FMLineBreakpoint()
    {
    }

    public FMLineBreakpoint( final IResource resource, final int line ) throws CoreException
    {
        final IWorkspaceRunnable runnable = new IWorkspaceRunnable()
        {
            private String createMessage( IResource resource )
            {
                return "Freemarker line breakpoint: " + resource.getName() + " [line: " + line + "]";
            }

            public void run( IProgressMonitor monitor ) throws CoreException
            {
                final IMarker marker = resource.createMarker( LiferayDebugCore.ID_FM_BREAKPOINT_TYPE );
                marker.setAttribute( IBreakpoint.ENABLED, Boolean.TRUE );
                marker.setAttribute( IMarker.LINE_NUMBER, line );
                marker.setAttribute( IBreakpoint.ID, getModelIdentifier() );
                marker.setAttribute( IMarker.MESSAGE, createMessage( resource ) );
                marker.setAttribute( "templateName", resource.getName() );

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
