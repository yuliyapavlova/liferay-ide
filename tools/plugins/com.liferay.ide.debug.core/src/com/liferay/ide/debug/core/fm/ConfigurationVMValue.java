package com.liferay.ide.debug.core.fm;

import freemarker.debug.DebugModel;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;


public class ConfigurationVMValue extends FMValue
{

    private IVariable[] variables;

    public ConfigurationVMValue( FMStackFrame stackFrame, DebugModel debugModel )
    {
        super( stackFrame, debugModel );
    }

    @Override
    public IVariable[] getVariables() throws DebugException
    {
        /*
         * Represents the debugger-side mirror of a debugged freemarker.core.Environment object in the remote VM.
         *
         * This interface extends DebugModel, and the properties of the Environment are exposed as hash keys on it.
         * Specifically, the following keys are supported: "currentNamespace", "dataModel", "globalNamespace",
         * "knownVariables", "mainNamespace", and "template".
         *
         * The debug model for the template supports keys
         * "configuration" and "name".
         *
         * The debug model for the configuration supports key "sharedVariables".
         * Additionally, all of the debug models for environment, template, and configuration also support all the
         * setting keys of freemarker.core.Configurable objects.
         */

        if( this.variables == null )
        {

        }

//      DebugModel name = template.get( "name" );
//      DebugModel configuration = template.get( "configuration" );

//      DebugModel sharedVariables = configuration.get( "sharedVariables" );

        return this.variables;
    }

    @Override
    public String getValueString() throws DebugException
    {
        // TODO Auto-generated method stub
        return super.getValueString();
    }
}
