package com.liferay.ide.hook.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;


public class CustomJSPCompareEditorInput extends CompareEditorInput
{

    public CustomJSPCompareEditorInput()
    {
        super( new CompareConfiguration() );

    }

    @Override
    protected Object prepareInput( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
