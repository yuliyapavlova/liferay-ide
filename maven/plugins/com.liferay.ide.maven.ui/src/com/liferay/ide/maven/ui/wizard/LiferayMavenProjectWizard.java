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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.m2e.core.ui.internal.wizards.MavenProjectWizard;
import org.eclipse.m2e.core.ui.internal.wizards.MavenProjectWizardArchetypeParametersPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;



/**
 * @author Gregory Amerson
 */
@SuppressWarnings( { "restriction" } )
public class LiferayMavenProjectWizard extends MavenProjectWizard
{
    protected LiferayMavenProjectWizardPage liferayMavenProjectPage;

    public LiferayMavenProjectWizard()
    {
        super();

        setWindowTitle( Msgs.newLiferayProject );
        setDefaultPageImageDescriptor( LiferayMavenImages.WIZ_NEW_PROJECT );
    }

    @Override
    public void addPages()
    {
        liferayMavenProjectPage = new LiferayMavenProjectWizardPage( importConfiguration, this.workingSets ) ;
        parametersPage = new MavenProjectWizardArchetypeParametersPage( importConfiguration );

        addPage( liferayMavenProjectPage );
        addPage( parametersPage );
    }

    @Override
    public void createPageControls( Composite pageContainer )
    {
        for( int i = 0; i < getPages().length; i++ )
        {
            IWizardPage page = (IWizardPage) getPages()[i];
            page.createControl( pageContainer );
            // page is responsible for ensuring the created control is
            // accessable
            // via getControl.
            Assert.isNotNull( page.getControl() );
        }
    }

    private static class Msgs extends NLS
    {
        public static String newLiferayProject;

        static
        {
            initializeMessages( LiferayMavenProjectWizard.class.getName(), Msgs.class );
        }
    }
}
