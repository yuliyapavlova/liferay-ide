
package com.liferay.ide.maven.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Gregory Amerson
 */
public class LiferayMavenImages
{
    // wizard images
    public static final ImageDescriptor WIZ_NEW_PROJECT = createDescriptor( "new_m2_project_wizard.gif" ); //$NON-NLS-1$

    private static ImageDescriptor createDescriptor( String key )
    {
        try
        {
            ImageRegistry imageRegistry = getImageRegistry();

            if( imageRegistry != null )
            {
                ImageDescriptor imageDescriptor = imageRegistry.getDescriptor( key );

                if( imageDescriptor == null )
                {
                    imageDescriptor = doCreateDescriptor( key );
                    imageRegistry.put( key, imageDescriptor );
                }

                return imageDescriptor;
            }
        }
        catch( Exception ex )
        {
            LiferayMavenUI.logError( key, ex );
        }

        return null;
    }

    private static ImageRegistry getImageRegistry()
    {
        LiferayMavenUI plugin = LiferayMavenUI.getDefault();
        return plugin == null ? null : plugin.getImageRegistry();
    }

    private static ImageDescriptor doCreateDescriptor( String image )
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin( LiferayMavenUI.PLUGIN_ID, "icons/" + image ); //$NON-NLS-1$
    }

}
