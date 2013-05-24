package com.liferay.ide.server.tomcat.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;


public class PortalSourceLookupParticipant extends AbstractSourceLookupParticipant
{

    public String getSourceName( Object object ) throws CoreException
    {
        return null;
    }

}
