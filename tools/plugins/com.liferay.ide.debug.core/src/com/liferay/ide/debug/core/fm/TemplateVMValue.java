package com.liferay.ide.debug.core.fm;

import freemarker.debug.DebugModel;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;


public class TemplateVMValue extends FMValue
{

    public TemplateVMValue( FMDebugTarget target, DebugModel debugModel )
    {
        super( target, debugModel );
    }

    @Override
    public IVariable[] getVariables() throws DebugException
    {
        return super.getVariables();
    }
}
