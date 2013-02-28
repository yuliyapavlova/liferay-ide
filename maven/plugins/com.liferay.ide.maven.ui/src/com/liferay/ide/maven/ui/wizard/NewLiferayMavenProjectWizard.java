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
 *******************************************************************************/
package com.liferay.ide.maven.ui.wizard;

import com.liferay.ide.maven.ui.LiferayMavenImages;

import org.eclipse.m2e.core.ui.internal.wizards.MavenProjectWizard;
import org.eclipse.osgi.util.NLS;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class NewLiferayMavenProjectWizard extends MavenProjectWizard
{

    protected LiferayMavenProjectWizardPage liferayMavenProjectPage;

    public NewLiferayMavenProjectWizard()
    {
        super();

        setWindowTitle( Msgs.newLiferayProjectWithMaven );
        setDefaultPageImageDescriptor( LiferayMavenImages.WIZ_NEW_PROJECT );
    }

    @Override
    public void addPages()
    {
        liferayMavenProjectPage = new LiferayMavenProjectWizardPage( importConfiguration );

        super.addPages();
    }

    private static class Msgs extends NLS
    {
        public static String newLiferayProjectWithMaven;

        static
        {
            initializeMessages( NewLiferayMavenProjectWizard.class.getName(), Msgs.class );
        }
    }
}
