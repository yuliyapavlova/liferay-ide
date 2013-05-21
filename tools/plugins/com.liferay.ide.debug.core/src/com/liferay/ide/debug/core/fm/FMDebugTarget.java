package com.liferay.ide.debug.core.fm;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.debug.core.ILRDebugConstants;

import freemarker.debug.Breakpoint;
import freemarker.debug.DebuggedEnvironment;
import freemarker.debug.Debugger;
import freemarker.debug.DebuggerClient;
import freemarker.debug.DebuggerListener;
import freemarker.debug.EnvironmentSuspendedEvent;

import java.net.Inet4Address;
import java.rmi.RemoteException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;


public class FMDebugTarget extends FMDebugElement implements IDebugTarget
{

    private String name;
    private ILaunch launch;
    private FMDebugTarget target;
    private IProcess process;
    private FMThread fmThread;
    private IThread[] threads;
    private EventDispatchJob eventDispatchJob;

    // suspend state
    private boolean suspended = true;

    // terminated state
    private boolean terminated = false;
    private Debugger debuggerClient;

    class EventDispatchJob extends Job implements DebuggerListener
    {
        private boolean setup;

        public EventDispatchJob()
        {
            super( "FM Event Dispatch" );
            setSystem( true );
        }

        @Override
        protected IStatus run( IProgressMonitor monitor )
        {
            while( ! isTerminated() )
            {
                // try to connect to debugger
                Debugger debugger = getDebuggerClient();

                if( debugger == null )
                {
                    try
                    {
                        Thread.sleep( 1000 );
                    }
                    catch( InterruptedException e )
                    {
                    }

                    continue;
                }

                if( !setup )
                {
                    setupDebugger(debugger);
                    setup = true;
                }

                synchronized( eventDispatchJob )
                {
                    try
                    {
                        wait();
                    }
                    catch( InterruptedException e )
                    {
                    }
                }
            }

            return Status.OK_STATUS;
        }

        private void setupDebugger(Debugger debugger)
        {
            try
            {
                Object o = debugger.addDebuggerListener( eventDispatchJob );
                System.out.println(o);
                // register breakpoints
                final IBreakpoint[] localBreakpoints =
                    DebugPlugin.getDefault().getBreakpointManager().getBreakpoints( getModelIdentifier() );

                for( IBreakpoint localBreakpoint : localBreakpoints )
                {
                    addRemoteBreakpoint( debugger, localBreakpoint );
                }

                final Breakpoint bp = new Breakpoint( "10153#10193#10712", 1 );
                debugger.addBreakpoint( bp );
            }
            catch( RemoteException e )
            {
            }
        }

        public void environmentSuspended( EnvironmentSuspendedEvent event ) throws RemoteException
        {
            int lineNumber = event.getLine();
            IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints( getModelIdentifier() );

            for( IBreakpoint breakpoint : breakpoints )
            {
                if( supportsBreakpoint( breakpoint ) )
                {
                    if( breakpoint instanceof ILineBreakpoint )
                    {
                        ILineBreakpoint lineBreakpoint = (ILineBreakpoint) breakpoint;

                        try
                        {
                            if( lineBreakpoint.getLineNumber() == lineNumber )
                            {
                                fmThread.setBreakpoints( new IBreakpoint[] { breakpoint } );
                                break;
                            }
                        }
                        catch( CoreException e )
                        {
                        }
                    }
                }
            }

            suspended( DebugEvent.BREAKPOINT );
        }
    }

    /**
     * Constructs a new debug target in the given launch for
     * the associated FM debugger
     *
     * @param launch containing launch
     * @param process Portal VM
     */
    public FMDebugTarget( ILaunch launch, IProcess process )
    {
        super( null );

        this.target = this;
        this.launch = launch;
        this.process = process;

        this.fmThread = new FMThread( this.target );
        this.threads = new IThread[] { this.fmThread };
        this.eventDispatchJob = new EventDispatchJob();
        this.eventDispatchJob.schedule();

        DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener( this );
    }

    public void addRemoteBreakpoint( Debugger debugger, IBreakpoint localBreakpoint ) throws RemoteException
    {
        String templateName = localBreakpoint.getMarker().getAttribute( "templateName", null );
        int line = localBreakpoint.getMarker().getAttribute( IMarker.LINE_NUMBER, -1 );

        if( ! CoreUtil.isNullOrEmpty( templateName ) && line > -1 )
        {
            Breakpoint remoteBreakpoint = new Breakpoint( templateName, line );
            debugger.addBreakpoint( remoteBreakpoint );
        }
    }

    public Debugger getDebuggerClient()
    {
        if( this.debuggerClient == null )
        {
            try
            {
                this.debuggerClient = DebuggerClient.getDebugger( Inet4Address.getByName( "localhost" ), 7600, "fmdebug" );
            }
            catch(Exception e )
            {
                e.printStackTrace();
            }

        }

        return this.debuggerClient;
    }

    public FMDebugTarget getDebugTarget()
    {
        return this.target;
    }

    public ILaunch getLaunch()
    {
        return this.launch;
    }

    public boolean canTerminate()
    {
        return getProcess().canTerminate();
    }

    public boolean isTerminated()
    {
        return getProcess().isTerminated();
    }

    public void terminate() throws DebugException
    {
        //TODO terminate
//        synchronized (fRequestSocket) {
//            fRequestWriter.println("exit");
//            fRequestWriter.flush();
//        }
    }

    public boolean canResume()
    {
        return !isTerminated() && isSuspended();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
     */
    public boolean canSuspend()
    {
        return !isTerminated() && !isSuspended();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
     */
    public boolean isSuspended()
    {
        return this.suspended;
    }

    public void resume() throws DebugException
    {
        //TODO resume
        //sendRequest("resume");
        System.out.println("resume");
        try
        {
            DebuggedEnvironment debuged = (DebuggedEnvironment) this.debuggerClient.getSuspendedEnvironments().iterator().next();

            debuged.resume();
        }
        catch( RemoteException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Notification the target has resumed for the given reason
     *
     * @param detail
     *            reason for the resume
     */
    private void resumed( int detail )
    {
        this.suspended = false;
        this.fmThread.fireResumeEvent( detail );
    }

    /**
     * Notification the target has suspended for the given reason
     *
     * @param detail
     *            reason for the suspend
     */
    private void suspended( int detail )
    {
        this.suspended = true;
        this.fmThread.fireSuspendEvent( detail );
    }

    public void suspend() throws DebugException
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
     */
    public void breakpointAdded( IBreakpoint breakpoint )
    {
        if( supportsBreakpoint( breakpoint ) )
        {
            try
            {
                if( breakpoint.isEnabled() )
                {
                    try
                    {
                        addRemoteBreakpoint( getDebuggerClient(), breakpoint );
                    }
                    catch( RemoteException e )
                    {
                        e.printStackTrace();
                    }
                }
            }
            catch( CoreException e )
            {
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint,
     * org.eclipse.core.resources.IMarkerDelta)
     */
    public void breakpointRemoved( IBreakpoint breakpoint, IMarkerDelta delta )
    {
        if( supportsBreakpoint( breakpoint ) )
        {
            System.out.println(breakpoint);
//            try
//            {
                //TODO add remove breakpoint
//                sendRequest( "clear " + ( (ILineBreakpoint) breakpoint ).getLineNumber() );
//            }
//            catch( CoreException e )
//            {
//            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint,
     * org.eclipse.core.resources.IMarkerDelta)
     */
    public void breakpointChanged( IBreakpoint breakpoint, IMarkerDelta delta )
    {
        if( supportsBreakpoint( breakpoint ) )
        {
            try
            {
                if( breakpoint.isEnabled() )
                {
                    breakpointAdded( breakpoint );
                }
                else
                {
                    breakpointRemoved( breakpoint, null );
                }
            }
            catch( CoreException e )
            {
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
     */
    public boolean canDisconnect()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
     */
    public void disconnect() throws DebugException
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
     */
    public boolean isDisconnected()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#supportsStorageRetrieval()
     */
    public boolean supportsStorageRetrieval()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long, long)
     */
    public IMemoryBlock getMemoryBlock( long startAddress, long length ) throws DebugException
    {
        return null;
    }

    public IProcess getProcess()
    {
        return this.process;
    }

    public IThread[] getThreads() throws DebugException
    {
        return this.threads;
    }

    public boolean hasThreads() throws DebugException
    {
        return this.threads != null && this.threads.length > 0;
    }

    public String getName() throws DebugException
    {
        if( this.name == null )
        {
            this.name = "FM Debugger";

//            this.name = getLaunch().getLaunchConfiguration().getAttribute( ILRDebugConstants.ATTR_FM_PROGRAM, "FM VM" );
        }

        return this.name;
    }

    public boolean supportsBreakpoint( IBreakpoint breakpoint )
    {
        if( breakpoint.getModelIdentifier().equals( ILRDebugConstants.ID_FM_DEBUG_MODEL ) )
        {
            System.out.println(breakpoint);
//            String program = getLaunch().getLaunchConfiguration().getAttribute(IPDAConstants.ATTR_PDA_PROGRAM, (String)null);
//            if (program != null) {
//                IMarker marker = breakpoint.getMarker();
//                if (marker != null) {
//                    IPath p = new Path(program);
//                    return marker.getResource().getFullPath().equals(p);
//                }
//            }
        }

        return false;
    }

    protected void step() throws DebugException
    {
        //TODO step()
//        sendRequest("step");
        System.out.println("step()");
    }

    /**
     * Called when this debug target terminates.
     */
    private void terminated()
    {
        this.terminated = true;
        this.suspended = false;

        DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);

        fireTerminateEvent();
    }

    protected IStackFrame[] getStackFrames()
    {
//        synchronized (fRequestSocket) {
//            fRequestWriter.println("stack");
//            fRequestWriter.flush();
//            try {
//                String framesData = fRequestReader.readLine();
//                if (framesData != null) {
//                    String[] frames = framesData.split("#");
//                    IStackFrame[] theFrames = new IStackFrame[frames.length];
//                    for (int i = 0; i < frames.length; i++) {
//                        String data = frames[i];
//                        theFrames[frames.length - i - 1] = new PDAStackFrame(fThread, data, i);
//                    }
//                    return theFrames;
//                }
//            } catch (IOException e) {
//                abort("Unable to retrieve stack frames", e);
//            }
//        }
        //TODO getStackFrames()
        return new IStackFrame[0];
    }

}
