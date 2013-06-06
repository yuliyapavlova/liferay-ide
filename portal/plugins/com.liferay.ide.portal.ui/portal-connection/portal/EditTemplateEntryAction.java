
package com.liferay.ide.server.ui.portal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.ui.LiferayServerUIPlugin;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * "Edit" menu action.
 */
public class EditTemplateEntryAction extends AbstractPortalAction
{

    public EditTemplateEntryAction( ISelectionProvider sp )
    {
        super( sp, "Edit Template Entry" ); //$NON-NLS-1$
    }

    @Override
    public void perform( Object entry )
    {
        if( entry instanceof TemplateEntry )
        {
            final TemplateEntry templateEntry = (TemplateEntry) entry;

            // TemplateEditorInput editorInput = new TemplateEditorInput( templateEntry );

            IFile file = CoreUtil.getProject( "Servers" ).getFile( templateEntry.getId() + ".ftl" ); //$NON-NLS-1$ //$NON-NLS-2$
            try
            {
                file.create( new ByteArrayInputStream( templateEntry.getContent().getBytes( "UTF-8" ) ), true, null ); //$NON-NLS-1$
            }
            catch( Exception e1 )
            {
                e1.printStackTrace();
            }

            FileEditorInput editorInput = new FileEditorInput( file );

            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            try
            {
                final IEditorPart editor =
                    page.openEditor( editorInput, "com.liferay.ide.freemarker.editor.FreemarkerEditor" ); //$NON-NLS-1$

                editor.addPropertyListener( new IPropertyListener()
                {
                    public void propertyChanged( Object source, int propId )
                    {
                        // if( source.equals( editor ) && propId == WorkflowDefinitionEditor.PROP_UPDATE_VERSION )
                        // {
                        // workflowEntry.getParent().clearCache();
                        // ( (CommonViewer) EditTemplateEntryAction.this.getSelectionProvider() ).refresh( true );
                        // }
                    }
                } );
            }
            catch( PartInitException e )
            {
                LiferayServerUIPlugin.logError( "Error opening template editor.", e ); //$NON-NLS-1$
            }
        }
    }

}
