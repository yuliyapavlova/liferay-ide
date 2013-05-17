package com.liferay.ide.debug.core.fm;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;


public class FMDebugTarget extends FMDebugElement implements IDebugTarget
{

    private ILaunch launch;
    private IDebugTarget target;
    private IProcess process;

    public FMDebugTarget( ILaunch launch, IProcess process )
    {
        super( null );

        this.launch = launch;
        this.target = this;
        this.process = process;
    }

    public IDebugTarget getDebugTarget()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ILaunch getLaunch()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getAdapter( Class adapter )
    {
        // TODO Auto-generated method stub
        return null;
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

    public void breakpointAdded( IBreakpoint breakpoint )
    {
        // TODO Auto-generated method stub

    }

    public void breakpointRemoved( IBreakpoint breakpoint, IMarkerDelta delta )
    {
        // TODO Auto-generated method stub

    }

    public void breakpointChanged( IBreakpoint breakpoint, IMarkerDelta delta )
    {
        // TODO Auto-generated method stub

    }

    public boolean canDisconnect()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void disconnect() throws DebugException
    {
        // TODO Auto-generated method stub

    }

    public boolean isDisconnected()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsStorageRetrieval()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public IMemoryBlock getMemoryBlock( long startAddress, long length ) throws DebugException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IProcess getProcess()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IThread[] getThreads() throws DebugException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasThreads() throws DebugException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public String getName() throws DebugException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean supportsBreakpoint( IBreakpoint breakpoint )
    {
        // TODO Auto-generated method stub
        return false;
    }

}
