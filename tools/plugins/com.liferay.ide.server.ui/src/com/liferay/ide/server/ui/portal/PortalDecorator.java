package com.liferay.ide.server.ui.portal;

import com.liferay.ide.core.remote.APIException;
import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;


public class PortalDecorator extends LabelProvider implements ILightweightLabelDecorator
{

    private static final String SITES_FOLDER_NAME = "Sites"; //$NON-NLS-1$


    public PortalDecorator()
    {
        super();
    }

    protected String combine( int version, int draftVersion )
    {
        if( draftVersion == -1 )
        {
            return "  [Version: " + version + "]"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        return "  [Version: " + version + ", Draft Version: " + draftVersion + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void decorate( Object element, IDecoration decoration )
    {
        if( element instanceof SitesFolder )
        {
            SitesFolder folder = (SitesFolder) element;

            IStatus status = folder.getStatus();

            if( status != null )
            {
                if( status.getException() instanceof APIException )
                {
                    decoration.addSuffix( "  [Error API unavailable. Ensure jsonws is configured.]" ); //$NON-NLS-1$
                    decoration.addOverlay( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                        ISharedImages.IMG_DEC_FIELD_ERROR ) );
                }
                else
                {
                    decoration.addSuffix( "  [" + status.getMessage() + "]" ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            else
            {
                decoration.addSuffix( "" ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof SiteFolder )
        {
            SiteFolder folder = (SiteFolder) element;

            String friendlyUrl = folder.getFriendlyUrl();

            if( ! CoreUtil.isNullOrEmpty( friendlyUrl ) )
            {
                decoration.addSuffix( " [" + friendlyUrl + "]" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        else if( element instanceof Node )
        {
            final Node node = (Node) element;
            final String id = node.getId();

            if( ! CoreUtil.isNullOrEmpty( id ) )
            {
                decoration.addSuffix( " [" + id + "]" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }

    @Override
    public Image getImage( Object element )
    {
        if( element instanceof RemoteFolder )
        {
            return PortalImages.IMG_SITES_FOLDER;
        }
        else if( element instanceof LoadingNode )
        {
            return PortalImages.IMG_LOADING;
        }
        else if( element instanceof TemplateEntry )
        {
            return PortalImages.IMG_USER_TEMPLATE;
        }
        else if( element instanceof IServer )
        {
            return null;
        }
        else
        {
            return null;
        }

//        return null;
    }

    public StyledString getStyledText( Object element )
    {
        String text = getText( element );

        if( ! CoreUtil.isNullOrEmpty( text ) )
        {
            return new StyledString( text );
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getText( Object element )
    {
        if( element instanceof SitesFolder )
        {
            return SITES_FOLDER_NAME;
        }
        else if( element instanceof Node )
        {
            Node node = (Node) element;

            return node.getDisplayName();
        }
        else if( element instanceof LoadingNode )
        {
            return "loading..."; //$NON-NLS-1$
        }
        else
        {
            return null;
        }
    }


}
