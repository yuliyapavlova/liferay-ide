package com.liferay.ide.debug.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.ISourceEditingTextTools;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.IBreakpointProvider;


@SuppressWarnings( "restriction" )
public class FMBreakpointProvider implements IBreakpointProvider
{

    private ISourceEditingTextTools fSourceEditingTextTools;

    public FMBreakpointProvider()
    {
    }

    public IStatus addBreakpoint( IDocument document, IEditorInput input, int lineNumber, int offset )
        throws CoreException
    {
        if( input instanceof IFileEditorInput )
        {
            IFileEditorInput fileEditorInput = (IFileEditorInput) input;
            
            fileEditorInput.getFile();
        }
        
        
        return null;
    }

    public IResource getResource( IEditorInput input )
    {
        return null;
    }


    public ISourceEditingTextTools getfSourceEditingTextTools()
    {
        return this.fSourceEditingTextTools;
    }

    public void setSourceEditingTextTools( ISourceEditingTextTools sourceEditingTextTools )
    {
        fSourceEditingTextTools = sourceEditingTextTools;
    }

}
