package com.liferay.ide.debug.core.fm;

import com.liferay.ide.debug.core.ILRDebugConstants;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;


public class FMDebugElement extends PlatformObject implements IDebugElement
{

    private FMDebugElement target;

    public FMDebugElement( FMDebugElement target )
    {
        this.target = target;
    }

    public String getModelIdentifier()
    {
        return ILRDebugConstants.ID_FM_DEBUG_MODEL;
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

}
