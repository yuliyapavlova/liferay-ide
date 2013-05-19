package com.liferay.ide.debug.core.fm;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;


public class FMStackFrame extends FMDebugElement implements IStackFrame
{

    public FMStackFrame( FMThread thread )
    {
        super( thread.getDebugTarget() );
    }

    public boolean canStepInto()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canStepOver()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canStepReturn()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isStepping()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void stepInto() throws DebugException
    {
        // TODO Auto-generated method stub

    }

    public void stepOver() throws DebugException
    {
        // TODO Auto-generated method stub

    }

    public void stepReturn() throws DebugException
    {
        // TODO Auto-generated method stub

    }

    public boolean canResume()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSuspend()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSuspended()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void resume() throws DebugException
    {
        // TODO Auto-generated method stub

    }

    public void suspend() throws DebugException
    {
        // TODO Auto-generated method stub

    }

    public boolean canTerminate()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isTerminated()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void terminate() throws DebugException
    {
        // TODO Auto-generated method stub

    }

    public IThread getThread()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IVariable[] getVariables() throws DebugException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasVariables() throws DebugException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public int getLineNumber() throws DebugException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getCharStart() throws DebugException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getCharEnd() throws DebugException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getName() throws DebugException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IRegisterGroup[] getRegisterGroups() throws DebugException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasRegisterGroups() throws DebugException
    {
        // TODO Auto-generated method stub
        return false;
    }

}
