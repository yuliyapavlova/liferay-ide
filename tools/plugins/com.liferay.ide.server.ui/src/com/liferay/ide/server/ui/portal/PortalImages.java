package com.liferay.ide.server.ui.portal;

import com.liferay.ide.server.ui.LiferayServerUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


public class PortalImages
{
    public static final Image IMG_LOADING = createImage( "e16/waiting_16x16.gif" ); //$NON-NLS-1$

    public static final Image IMG_USER_TEMPLATE = createImage( "template_obj.gif" ); //$NON-NLS-1$

    public static final Image IMG_SITES_FOLDER = createImage( "e16/sites_16x16.png" ); //$NON-NLS-1$

    private static ImageDescriptor create( String key )
    {
        try
        {
            ImageDescriptor imageDescriptor = createDescriptor( key );
            ImageRegistry imageRegistry = getImageRegistry();

            if( imageRegistry != null )
            {
                imageRegistry.put( key, imageDescriptor );
            }

            return imageDescriptor;
        }
        catch( Exception ex )
        {
            LiferayServerUIPlugin.logError( ex );
            return null;
        }
    }

    private static ImageDescriptor createDescriptor( String image )
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin( LiferayServerUIPlugin.PLUGIN_ID, "icons/" + image ); //$NON-NLS-1$
    }

    private static Image createImage( String key )
    {
        create( key );
        ImageRegistry imageRegistry = getImageRegistry();

        return imageRegistry == null ? null : imageRegistry.get( key );
    }

    private static ImageRegistry getImageRegistry()
    {
        LiferayServerUIPlugin plugin = LiferayServerUIPlugin.getDefault();
        return plugin == null ? null : plugin.getImageRegistry();
    }
}
