package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.debug.core.ILRDebugConstants;
import com.liferay.ide.debug.core.fm.FMLineBreakpoint;
import com.liferay.ide.debug.core.fm.FMStackFrame;
import com.liferay.ide.debug.core.fm.FMThread;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;


public class PortalSourceLookupParticipant extends AbstractSourceLookupParticipant
{

    public String getSourceName( Object object ) throws CoreException
    {
        String retval = null;

        if( object instanceof FMStackFrame )
        {
            try
            {
                FMStackFrame frame = (FMStackFrame) object;

                String name = frame.getName();
                FMThread thread = (FMThread) frame.getThread();

                FMLineBreakpoint bp = (FMLineBreakpoint)thread.getBreakpoints()[0];
                String templateName = bp.getMarker().getAttribute( ILRDebugConstants.FM_TEMPLATE_NAME, null );

                IPath templatePath = new Path( templateName ).removeFirstSegments( 1 );

                retval = templatePath.toPortableString();
            }
            catch( Exception e )
            {

            }

        }
        return retval;
    }

}
