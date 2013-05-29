package com.liferay.ide.server.ui.portal;

import com.liferay.ide.core.remote.APIException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


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
    }

    @Override
    public Image getImage( Object element )
    {
        if( element instanceof SitesFolder || element instanceof SiteFolder )
        {
            return PortalImages.IMG_SITES_FOLDER;
        }
//        else if( element instanceof WorkflowDefinitionEntry )
//        {
//            WorkflowDefinitionEntry definition = (WorkflowDefinitionEntry) element;
//
//            if( definition.isLoadingNode() )
//            {
//                return KaleoImages.IMG_LOADING;
//            }
//            else
//            {
//                return KaleoImages.IMG_WORKFLOW_DEFINITION;
//            }
//        }

        return null;
    }

    public StyledString getStyledText( Object element )
    {
        if( element instanceof SitesFolder )
        {
            return new StyledString( SITES_FOLDER_NAME );
        }
//        else if( element instanceof WorkflowDefinitionEntry )
//        {
//            WorkflowDefinitionEntry definitionNode = (WorkflowDefinitionEntry) element;
//            return new StyledString( definitionNode.getName() );
//        }
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
//        else if( element instanceof WorkflowDefinitionEntry )
//        {
//            WorkflowDefinitionEntry definitionNode = (WorkflowDefinitionEntry) element;
//
//            return definitionNode.getName();
//        }
        else
        {
            return null;
        }
    }


}
