
package com.liferay.ide.server.ui.portal;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractPortalAction extends SelectionProviderAction
{

    protected Shell shell;

    public AbstractPortalAction( ISelectionProvider selectionProvider, String text )
    {
        this( null, selectionProvider, text );
    }

    public AbstractPortalAction( Shell shell, ISelectionProvider selectionProvider, String text )
    {
        super( selectionProvider, text );
        this.shell = shell;
        setEnabled( false );
    }

    public boolean accept( Object node )
    {
        return node instanceof Node;
    }

    public Shell getShell()
    {
        return this.shell;
    }

    public abstract void perform( Object node );

    @SuppressWarnings( "rawtypes" )
    public void run()
    {
        Iterator iterator = getStructuredSelection().iterator();

        if( !iterator.hasNext() )
            return;

        Object obj = iterator.next();

        if( accept( obj ) )
        {
            perform( obj );
        }

        selectionChanged( getStructuredSelection() );
    }

    /**
     * Update the enabled state.
     *
     * @param sel
     *            a selection
     */
    @SuppressWarnings( "rawtypes" )
    public void selectionChanged( IStructuredSelection sel )
    {
        if( sel.isEmpty() )
        {
            setEnabled( false );
            return;
        }

        boolean enabled = false;
        Iterator iterator = sel.iterator();

        while( iterator.hasNext() )
        {
            Object obj = iterator.next();

            if( obj instanceof TemplateEntry )
            {
                TemplateEntry node = (TemplateEntry) obj;

                if( accept( node ) )
                {
                    enabled = true;
                }
            }
            else if( obj instanceof RemoteFolder )
            {
                RemoteFolder node = (RemoteFolder) obj;

                if( accept( node ) )
                {
                    enabled = true;
                }
            }
            else
            {
                setEnabled( false );
                return;
            }
        }

        setEnabled( enabled );
    }
}
