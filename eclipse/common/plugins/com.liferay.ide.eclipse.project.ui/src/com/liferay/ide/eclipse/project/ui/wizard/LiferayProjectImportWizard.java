/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.project.ui.wizard;

import com.liferay.ide.eclipse.project.core.LiferayProjectImportDataModelProvider;
import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.ui.wizard.INewProjectWizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class LiferayProjectImportWizard extends DataModelWizard implements IWorkbenchWizard, INewProjectWizard {

	protected LiferayProjectImportWizardPage liferayProjectImportWizardPage;

	public LiferayProjectImportWizard() {
		this(null);
	}

	public LiferayProjectImportWizard(IDataModel dataModel) {
		super(dataModel);
		
		setWindowTitle("Import Project");
		
		setDefaultPageImageDescriptor(ProjectUIPlugin.imageDescriptorFromPlugin(
			ProjectUIPlugin.PLUGIN_ID, "/icons/wizban/import_wiz.png"));
	}

	@Override
	public boolean canFinish() {
		return getDataModel().isValid();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	protected void doAddPages() {
		liferayProjectImportWizardPage = new LiferayProjectImportWizardPage(getDataModel(), "pageOne");
		
		addPage(liferayProjectImportWizardPage);
	}

	@Override
	protected IDataModelProvider getDefaultProvider() {
		return new LiferayProjectImportDataModelProvider();
	}

	@Override
	protected boolean runForked() {
		return false;
	}


	public void setProjectType(String projectType) {

	}

}