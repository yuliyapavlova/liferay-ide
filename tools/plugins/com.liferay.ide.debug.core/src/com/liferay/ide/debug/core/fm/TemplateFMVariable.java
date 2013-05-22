package com.liferay.ide.debug.core.fm;

import freemarker.debug.DebugModel;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;


public class TemplateFMVariable extends FMVariable
{

    public TemplateFMVariable( FMStackFrame stackFrame, DebugModel debugModel )
    {
        super( stackFrame, "template", debugModel );
    }

    @Override
    public IValue getValue() throws DebugException
    {
        return new TemplateVMValue( getDebugTarget(), getDebugModel() );
    }
}
