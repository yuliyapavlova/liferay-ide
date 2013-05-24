package com.liferay.ide.server.tomcat.core;

import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;


@SuppressWarnings( "restriction" )
public class PortalSourceLookupDirector extends JavaSourceLookupDirector
{

    @Override
    public void initializeParticipants()
    {
        super.initializeParticipants();

        addParticipants( new ISourceLookupParticipant[] { new PortalSourceLookupParticipant() } );
    }

}
