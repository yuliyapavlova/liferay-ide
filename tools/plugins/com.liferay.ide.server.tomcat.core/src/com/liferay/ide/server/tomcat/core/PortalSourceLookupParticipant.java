package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.debug.core.ILRDebugConstants;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;


public class PortalSourceLookupParticipant extends AbstractSourceLookupParticipant
{

    @Override
    public Object[] findSourceElements( Object object ) throws CoreException
    {
        Object[] retval = null;

        final Object[] sourceElements = super.findSourceElements( object );

        if( object instanceof IStackFrame )
        {
            String templateName = getTemplateName( (IStackFrame) object  );

            IProject project = getSourceProject( templateName );

            if( project != null )
            {
                // go through all containers and only include the ones in this project
                List<Object> validSourceElements = new ArrayList<Object>();

                for( Object sourceElement : sourceElements )
                {
                    if( sourceElement instanceof IResource )
                    {
                        IResource res = (IResource) sourceElement;

                        if(res.getProject().equals( project ))
                        {
                            validSourceElements.add( sourceElement );
                        }
                    }
                }

                retval = validSourceElements.toArray(  new Object[0] );
            }
        }

        if( retval == null )
        {
            retval = sourceElements;
        }

        return retval;
    }

    private  IProject getSourceProject( String templateName )
    {
        IProject retval = null;

        if( ! CoreUtil.isNullOrEmpty( templateName ) )
        {
            String projectName = new Path(templateName).segment( 0 );

            IProject project = CoreUtil.getProject( projectName.replaceAll( "_SERVLET_CONTEXT_", "" ) ); //$NON-NLS-1$ //$NON-NLS-2$

            if( ServerUtil.isLiferayProject( project ) )
            {
                retval = project;
            }
        }

        return retval;
    }

    public String getSourceName( Object object ) throws CoreException
    {
        String retval = null;

        if( object instanceof IStackFrame )
        {
            try
            {
                IStackFrame frame = (IStackFrame) object;

                String templateName = getTemplateName( frame );

                IPath templatePath = new Path( templateName ).removeFirstSegments( 1 );

//                String contextPath = new Path( templateName ).segment( 0 ).replaceAll( "_SERVLET_CONTEXT_", "" );  //$NON-NLS-1$//$NON-NLS-2$

                retval = "_diffs/" + templatePath.toPortableString(); //$NON-NLS-1$
            }
            catch( Exception e )
            {
            }
        }

        return retval;
    }

    private String getTemplateName( IStackFrame frame ) throws DebugException
    {
        String retval = null;
        String name = frame.getName();
        IThread thread = frame.getThread();

        IBreakpoint bp = thread.getBreakpoints()[0];

        return bp.getMarker().getAttribute( ILRDebugConstants.FM_TEMPLATE_NAME, null );
    }



}
