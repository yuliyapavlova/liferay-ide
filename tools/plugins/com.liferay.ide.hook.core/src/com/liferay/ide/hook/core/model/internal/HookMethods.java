/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.model.PortalPropertiesFile;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Gregory Amerson
 */
public class HookMethods
{

    public static IFile getPortalPropertiesFile( Hook hook )
    {
        return getPortalPropertiesFile( hook, true );
    }

    public static IFile getPortalPropertiesFile( Hook hook, boolean onlyIfExists )
    {
        IFile retval = null;

        if( hook != null )
        {
            PortalPropertiesFile portalPropertiesFileElement =
                hook.getPortalPropertiesFile().nearest( PortalPropertiesFile.class );

            if( portalPropertiesFileElement != null )
            {
                Path filePath = portalPropertiesFileElement.getValue().content();

                for( IFolder folder : ProjectUtil.getSourceFolders( hook.adapt( IProject.class ) ) )
                {
                    IFile file = folder.getFile( filePath.toPortableString() );

                    if( onlyIfExists )
                    {
                        if( file.exists() )
                        {
                            retval = file;
                        }
                    }
                    else
                    {
                        retval = file;
                    }
                }
            }
        }

        return retval;
    }
}
