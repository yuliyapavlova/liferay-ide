package com.liferay.ide.maven.ui.wizard;

import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.ui.internal.wizards.AbstractMavenWizardPage;
import org.eclipse.swt.widgets.Composite;


@SuppressWarnings( "restriction" )
public class LiferayMavenProjectWizardPage extends AbstractMavenWizardPage
{

    public LiferayMavenProjectWizardPage( ProjectImportConfiguration importConfiguration )
    {
        super( "LiferayMavenProjectWizardPage", importConfiguration ); //$NON-NLS-1$
    }

    public void createControl( Composite parent )
    {
        // TODO Auto-generated method stub

    }

}
