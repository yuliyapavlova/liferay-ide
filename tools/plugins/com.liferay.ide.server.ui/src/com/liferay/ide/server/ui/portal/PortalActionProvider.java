
package com.liferay.ide.server.ui.portal;

import com.liferay.ide.server.ui.LiferayServerUIPlugin;

import java.util.Iterator;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

public class PortalActionProvider extends CommonActionProvider
{

    public static final String NEW_MENU_ID = "org.eclipse.wst.server.ui.internal.cnf.newMenuId"; //$NON-NLS-1$
    public static final String TOP_SECTION_END_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionEnd"; //$NON-NLS-1$
    public static final String TOP_SECTION_START_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionStart"; //$NON-NLS-1$

    private ICommonActionExtensionSite actionSite;
    private EditTemplateEntryAction editAction;
    private RefreshPortalAction refreshAction;

    public PortalActionProvider()
    {
        super();
    }

    private void addListeners( CommonViewer tableViewer )
    {
        tableViewer.addOpenListener( new IOpenListener()
        {

            public void open( OpenEvent event )
            {
                try
                {
                    IStructuredSelection sel = (IStructuredSelection) event.getSelection();
                    Object data = sel.getFirstElement();

                    if( !( data instanceof TemplateEntry ) )
                    {
                        return;
                    }

                    PortalActionProvider.this.editAction.run();

                }
                catch( Exception e )
                {
                    LiferayServerUIPlugin.logError( "Error opening template entry.", e ); //$NON-NLS-1$
                }
            }
        } );
    }

    protected void addTopSection( IMenuManager menu, TemplateEntry templateEntry, RemoteFolder remoteFolder )
    {
        // open action
        if( templateEntry != null )
        {
            menu.add( editAction );
        }

        if( remoteFolder != null )
        {
            menu.add( refreshAction );
        }
    }

    public void fillContextMenu( IMenuManager menu )
    {
        // This is a temp workaround to clean up the default group that are provided by CNF
        menu.removeAll();

        ICommonViewerSite site = actionSite.getViewSite();
        IStructuredSelection selection = null;

        if( site instanceof ICommonViewerWorkbenchSite )
        {
            ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite) site;
            selection = (IStructuredSelection) wsSite.getSelectionProvider().getSelection();
        }

        TemplateEntry templateEntry = null;
        RemoteFolder remoteFolder = null;

        if( selection != null && !selection.isEmpty() )
        {
            Iterator iterator = selection.iterator();
            Object obj = iterator.next();

            if( obj instanceof TemplateEntry )
            {
                templateEntry = (TemplateEntry) obj;
            }

            if( obj instanceof RemoteFolder )
            {
                remoteFolder = (RemoteFolder) obj;
            }

            if( iterator.hasNext() )
            {
                templateEntry = null;
                remoteFolder = null;
            }
        }

        menu.add( invisibleSeparator( TOP_SECTION_START_SEPARATOR ) );
        addTopSection( menu, templateEntry, remoteFolder );
        menu.add( invisibleSeparator( TOP_SECTION_END_SEPARATOR ) );
        menu.add( new Separator() );

        menu.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
        menu.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS + "-end" ) ); //$NON-NLS-1$
    }

    public void init( ICommonActionExtensionSite site )
    {
        super.init( site );
        this.actionSite = site;
        ICommonViewerSite viewerSite = site.getViewSite();

        if( viewerSite instanceof ICommonViewerWorkbenchSite )
        {
            StructuredViewer v = site.getStructuredViewer();

            if( v instanceof CommonViewer )
            {
                CommonViewer cv = (CommonViewer) v;
                ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite) viewerSite;
                addListeners( cv );
                makeActions( cv, wsSite.getSelectionProvider() );
            }
        }
    }

    private Separator invisibleSeparator( String s )
    {
        Separator sep = new Separator( s );
        sep.setVisible( false );
        return sep;
    }

    private void makeActions( CommonViewer tableViewer, ISelectionProvider provider )
    {
        // Shell shell = tableViewer.getTree().getShell();

        // create the open action
        editAction = new EditTemplateEntryAction( provider );
        refreshAction = new RefreshPortalAction( provider );

        // create copy, paste, and delete actions
        // pasteAction = new PasteAction(shell, provider, clipboard);
        // copyAction = new CopyAction(provider, clipboard, pasteAction);
        // globalDeleteAction = new GlobalDeleteAction(shell, provider);
        // renameAction = new RenameAction(shell, tableViewer, provider);

    }

}
