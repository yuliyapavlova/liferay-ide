package com.liferay.ide.debug.core.fm;

import freemarker.debug.DebugModel;
import freemarker.template.TemplateModelException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;


public class FMValue extends FMDebugElement implements IValue
{
    private static final int VALID_VARIBLE_TYPES = DebugModel.TYPE_BOOLEAN | DebugModel.TYPE_COLLECTION |
        DebugModel.TYPE_CONFIGURATION | DebugModel.TYPE_DATE | DebugModel.TYPE_HASH | DebugModel.TYPE_HASH_EX |
        DebugModel.TYPE_NUMBER | DebugModel.TYPE_SCALAR | DebugModel.TYPE_SEQUENCE | DebugModel.TYPE_TEMPLATE;

    protected DebugModel debugModel;
    protected FMStackFrame stackFrame;
    private IVariable[] variables;

    public FMValue( FMStackFrame stackFrame, DebugModel debugModel )
    {
        super( stackFrame.getDebugTarget() );

        this.stackFrame = stackFrame;
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
                retval = getHashValueString( this.debugModel );
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
                retval = getSequenceValueString( this.debugModel );
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

    private String getSequenceValueString( DebugModel model )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( '[' );

        try
        {
            for( int i = 0; i < model.size(); i++ )
            {
                final DebugModel val = model.get( i );
                final String value = getModelValueString( val );

                if( value != null )
                {
                    sb.append( value );
                    sb.append(',');
                }
            }
        }
        catch( Exception e )
        {
            sb.append( e.getMessage() );
        }

        String value = sb.toString();

        return value.endsWith( "," ) ? value.replaceFirst( ",$", "]" ) : value;
    }

    private String getModelValueString( DebugModel model ) throws RemoteException, TemplateModelException
    {
        String value = null;

        final int modelTypes = model.getModelTypes();

        if( isStringType( modelTypes ) && !isHashType( modelTypes ) )
        {
            value = model.getAsString();
        }
        else if( isNumberType( modelTypes ) )
        {
            value = model.getAsNumber().toString();
        }
        else if( isDateType( modelTypes ) )
        {
            value = model.getAsDate().toString();
        }
        else if( isHashType( modelTypes ) )
        {
            value = getHashValueString( model );
        }
        else if( isCollectionType( modelTypes ) )
        {
            value = getHashValueString( model );
        }
        else if( isMethodType( modelTypes) )
        {
            value = null;
        }
        else
        {
            System.out.println("unsupported model type: " + modelTypes );
        }

        return value;
    }

    private String getHashValueString( DebugModel model )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( '{' );

        try
        {
            for( String key : model.keys() )
            {
                final DebugModel val = model.get( key );
                final String value = getModelValueString( val );

                if( value != null )
                {
                    sb.append( key );
                    sb.append('=');
                    sb.append( value );
                    sb.append(',');
                }
            }
        }
        catch( Exception e )
        {
            sb.append( e.getMessage() );
        }

        String value = sb.toString();

        return value.endsWith( "," ) ? value.replaceFirst( ",$", "}" ) : value;
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
            List<IVariable> vars = new ArrayList<IVariable>();

            try
            {
                int types = this.debugModel.getModelTypes();

                if( isHashType( types) )
                {
                    String[] keys = this.debugModel.keys();

                    DebugModel[] vals = this.debugModel.get( keys );

                    for( int i = 0; i < keys.length; i++ )
                    {
                        DebugModel hashValue = vals[i];

                        if( isValidVariable( hashValue ) )
                        {
                            vars.add( new FMVariable( stackFrame, keys[i] , hashValue ) );
                        }
                    }
                }
                else if( isCollectionType( types ) )
                {
                    String[] keys = this.debugModel.keys();

                    System.out.println(keys);

//                    if( isValidVariable( hashValue ) )
//                    {
//                        vars.add( new FMVariable( stackFrame, key , debugModel ) );
//                    }
                }
                else if( isStringType( types ) || isNumberType( types ) )
                {
                    // no variables
                }
                else
                {
                    System.out.println( getReferenceTypeName( this.debugModel ) );
                }
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }

            this.variables = vars.toArray( new IVariable[vars.size()] );
        }

        return this.variables;
    }

    private boolean isHashType( int types )
    {
        return ( DebugModel.TYPE_HASH & types ) > 0 || ( DebugModel.TYPE_HASH_EX & types ) > 0;
    }

    private boolean isMethodType( int types )
    {
        return ( DebugModel.TYPE_METHOD & types ) > 0 || ( DebugModel.TYPE_METHOD_EX & types ) > 0;
    }

    private boolean isStringType( int types )
    {
        return ( DebugModel.TYPE_SCALAR & types ) > 0;
    }

    private boolean isNumberType( int types )
    {
        return ( DebugModel.TYPE_NUMBER & types ) > 0;
    }

    private boolean isDateType( int types )
    {
        return ( DebugModel.TYPE_DATE & types ) > 0;
    }

    private boolean isCollectionType( int types )
    {
        return ( DebugModel.TYPE_COLLECTION & types ) > 0;
    }

    private boolean isValidVariable( DebugModel model )
    {
        boolean retval = false;

        if( model != null )
        {
            try
            {
                int types = model.getModelTypes();

                retval = ( VALID_VARIBLE_TYPES & types ) > 0;
            }
            catch( RemoteException e )
            {
                e.printStackTrace();
            }
        }

        return retval;
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
