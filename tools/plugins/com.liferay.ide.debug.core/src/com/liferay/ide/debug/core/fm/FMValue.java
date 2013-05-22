package com.liferay.ide.debug.core.fm;

import freemarker.debug.DebugModel;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;


public class FMValue extends FMDebugElement implements IValue
{

    private DebugModel debugModel;
    private IVariable[] variables;

    public FMValue( FMDebugTarget target, DebugModel debugModel )
    {
        super( target );

        this.debugModel = debugModel;
    }



    public String getValueString() throws DebugException
    {
        String retval = null;

        try
        {
            int types = this.debugModel.getModelTypes();

            if( ( DebugModel.TYPE_BOOLEAN & types ) > 0 )
            {
                retval = Boolean.toString( this.debugModel.getAsBoolean() );
            }

            if( ( DebugModel.TYPE_COLLECTION & types ) > 0 )
            {
                retval = "Collection";
            }

            if( ( DebugModel.TYPE_CONFIGURATION & types ) > 0 )
            {
                retval = "Configuration";
            }

            if( ( DebugModel.TYPE_DATE & types ) > 0 )
            {
                retval = this.debugModel.getAsDate().toString();
            }

            if( ( DebugModel.TYPE_ENVIRONMENT & types ) > 0 )
            {
                retval = "Environment";
            }

            if( ( DebugModel.TYPE_HASH & types ) > 0 )
            {
                retval = "Hash";
            }

            if( ( DebugModel.TYPE_HASH_EX & types ) > 0 )
            {
                retval = "Hash_ex";
            }

            if( ( DebugModel.TYPE_METHOD & types ) > 0 )
            {
                retval = "Method";
            }

            if( ( DebugModel.TYPE_METHOD_EX & types ) > 0 )
            {
                retval = "Method_ex";
            }

            if( ( DebugModel.TYPE_NUMBER & types ) > 0 )
            {
                retval = this.debugModel.getAsNumber().toString();
            }

            if( ( DebugModel.TYPE_SCALAR & types ) > 0 )
            {
                retval = this.debugModel.getAsString();
            }

            if( ( DebugModel.TYPE_SEQUENCE & types ) > 0 )
            {
                retval = "sequence";
            }

            if( ( DebugModel.TYPE_TEMPLATE & types ) > 0 )
            {
                retval = "template";
            }

            if( ( DebugModel.TYPE_TRANSFORM & types ) > 0 )
            {
                retval = "transform";
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        if( retval == null )
        {
            retval = "";
        }

        return retval;
    }

    public boolean isAllocated() throws DebugException
    {
        return true;
    }

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

    public boolean hasVariables() throws DebugException
    {
        return getVariables().length > 0;
    }

    public String getReferenceTypeName() throws DebugException
    {
        return getReferenceTypeName( this.debugModel );
    }

}
