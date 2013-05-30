
package com.liferay.ide.server.ui.portal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;

public class TemplateStorage extends PlatformObject implements IStorage
{

	private TemplateEntry node;

	public TemplateStorage( TemplateEntry defNode )
	{
		this.node = defNode;
	}

	public InputStream getContents() throws CoreException
	{
		return new ByteArrayInputStream( node.getContent() != null ? node.getContent().getBytes() : "".getBytes() ); //$NON-NLS-1$
	}

	public IPath getFullPath()
	{
//		return new Path( node.getLocation() + "/KaleoWorkflowDefinitions/" + this.node.getName() );
        return new Path( this.node.getDisplayName() );
	}

	public String getName()
	{
		return this.node.getDisplayName();
	}

	public boolean isReadOnly()
	{
		return false;
	}

}
