
package com.liferay.ide.server.ui.portal;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * A storage editor input that can read a template
 *
 * @author <a href="mailto:gregory.amerson@liferay.com">Gregory Amerson</a>
 */
public class TemplateEditorInput extends PlatformObject implements IStorageEditorInput
{

	private TemplateEntry defNode;

	public TemplateEditorInput( TemplateEntry defNode )
	{
		super();
		this.defNode = defNode;
	}

	public boolean exists()
	{
		return true;
		// return this.modelElement != null && this.valueProperty != null;
	}

	public TemplateEntry getTemplateEntry()
	{
		return this.defNode;
	}

	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	public String getName()
	{
	    return this.defNode.getId();
	}

	public IPersistableElement getPersistable()
	{
		return null;
	}

	public IStorage getStorage()
	{
		return new TemplateStorage( this.defNode );
	}

	public String getToolTipText()
	{
		return getName();
	}

	public void setTemplateEntry( TemplateEntry newNode )
	{
		this.defNode = newNode;
	}

}
