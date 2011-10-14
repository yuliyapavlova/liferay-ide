/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.eclipse.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.sdk.SDKManager;
import com.liferay.ide.eclipse.sdk.pref.SDKsPreferencePage;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.equinox.internal.p2.discovery.DiscoveryCore;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogCategory;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.equinox.internal.p2.discovery.model.Tag;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogFilter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectFirstPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.PageBook;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.ui.internal.FacetsSelectionDialog;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class NewPluginProjectFirstPage extends WebProjectFirstPage implements IPluginProjectDataModelProperties {

	protected Button extType;

	protected Button hookType;

	protected Button layoutTemplateType;

	// private DataModelSynchHelper modelHelper;

	protected Button portletType;

	protected boolean shouldValidatePage = true;

	protected Button themeType;

	protected Button liferayDefaultTemplate;

	protected Button marketplaceTemplate;

	public NewPluginProjectFirstPage(NewPluginProjectWizard wizard, IDataModel model, String pageName) {
		super(model, pageName);

		this.setImageDescriptor(wizard.getDefaultPageImageDescriptor());
		this.setTitle("Liferay Plugin Project");
		this.setDescription("Create a new plugin project for Liferay Portal.");

		primaryProjectFacet = IPluginFacetConstants.LIFERAY_PORTLET_PROJECT_FACET;

	}

	@Override
	public boolean canFlipToNextPage() {
		boolean canFlip = super.canFlipToNextPage();

		if (canFlip) {
			// Only the portlet mode needs a 2ne page
			return getModel().getBooleanProperty(PLUGIN_TYPE_PORTLET);
		}

		return canFlip;
	}

	public void setShouldValidatePage(boolean shouldValidate) {
		this.shouldValidatePage = shouldValidate;
	}

	protected void configureSDKsLinkSelected(SelectionEvent e) {
		// boolean noSDKs = SDKManager.getInstance().getSDKs().length == 0;

		int retval = PreferencesUtil.createPreferenceDialogOn(this.getShell(), SDKsPreferencePage.ID, new String[] {
			SDKsPreferencePage.ID
		}, null).open();

		if (retval == Window.OK) {
			getModel().notifyPropertyChange(LIFERAY_SDK_NAME, IDataModel.VALID_VALUES_CHG);
		}
	}

	protected Group createDefaultGroup(Composite parent, String text, int columns) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(text);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		GridLayout gl = new GridLayout(columns, false);

		group.setLayout(gl);

		return group;
	}

	protected void createImportProjectLink(Composite parent) {
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.END;
		gd.horizontalIndent = 8;
		gd.verticalIndent = 8;

		Link link = new Link(parent, SWT.UNDERLINE_LINK);
		link.setText("<a href=\"#\">Create a new project from existing sources...</a>");
		link.setLayoutData(gd);
		link.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getWizard().getContainer().getShell().close();

				WizardDialog dialog = new WizardDialog(getShell(), new NewProjectFromSourceWizard());
				dialog.open();
			}

		});
	}

	protected void createLiferayRuntimeGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText("Configuration");
		group.setLayoutData(gdhfill());
		group.setLayout(new GridLayout(3, false));

		getModel().addListener(this);

		SelectionAdapter selectionAdapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (SDKManager.getInstance().getDefaultSDK() != null &&
					getModel().getBooleanProperty(LIFERAY_USE_SDK_LOCATION)) {
					// toggling the location property will get the location field to
					// update
					getModel().setBooleanProperty(LIFERAY_USE_SDK_LOCATION, false);
					getModel().setBooleanProperty(LIFERAY_USE_SDK_LOCATION, true);
				}

				validatePage(true);
			}
		};

		new LiferaySDKField(group, getModel(), selectionAdapter, LIFERAY_SDK_NAME, this.synchHelper);

		// SWTUtil.createLabel(group, "Liferay Plugins SDK", 1);
		//
		// Combo sdkCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		// sdkCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		//
		// Link configureSDKsLink = new Link(group, SWT.UNDERLINE_LINK);
		// configureSDKsLink.setText("<a href=\"#\">Configure</a>");
		// configureSDKsLink.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		// configureSDKsLink.addSelectionListener(selectionAdapter);
		// this.synchHelper.synchCombo(sdkCombo, LIFERAY_SDK_NAME, new Control[] {
		// configureSDKsLink
		// });

		SWTUtil.createLabel(group, "Liferay Portal Runtime", 1);
		serverTargetCombo = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
		serverTargetCombo.setLayoutData(gdhfill());

		Button newServerTargetButton = new Button(group, SWT.PUSH);
		newServerTargetButton.setText("New ...");
		newServerTargetButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				final DataModelPropertyDescriptor[] preAdditionDescriptors =
					model.getValidPropertyDescriptors(FACET_RUNTIME);

				boolean isOK = ServerUIUtil.showNewRuntimeWizard(getShell(), getModuleTypeID(), null, "com.liferay.");

				if (isOK) {
					DataModelPropertyDescriptor[] postAdditionDescriptors =
						model.getValidPropertyDescriptors(FACET_RUNTIME);

					Object[] preAddition = new Object[preAdditionDescriptors.length];

					for (int i = 0; i < preAddition.length; i++) {
						preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
					}

					Object[] postAddition = new Object[postAdditionDescriptors.length];

					for (int i = 0; i < postAddition.length; i++) {
						postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
					}

					Object newAddition = CoreUtil.getNewObject(preAddition, postAddition);

					if (newAddition != null) // can this ever be null?
						model.setProperty(FACET_RUNTIME, newAddition);
				}
			}
		});

		Control[] deps = new Control[] {
			newServerTargetButton
		};
		synchHelper.synchCombo(serverTargetCombo, FACET_RUNTIME, deps);
		if (serverTargetCombo.getSelectionIndex() == -1 && serverTargetCombo.getVisibleItemCount() != 0) {
			serverTargetCombo.select(0);
		}

		SWTUtil.createLabel(group, "", 1);

		Link facetsLink = new Link(group, SWT.UNDERLINE_LINK);
		GridData gd = new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, 1, 1);
		facetsLink.setLayoutData(gd);
		facetsLink.setText("<a href=\"#\">Advanced project configuration...</a>");
		facetsLink.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FacetsSelectionDialog.openDialog(
					getShell(), (IFacetedProjectWorkingCopy) getModel().getProperty(FACETED_PROJECT_WORKING_COPY));
			}
		});
		SWTUtil.createLabel(group, "", 1);
	}

	protected void createProjectTemplateGroup( Composite parent ) {
		Group group = SWTUtil.createGroup( parent, "Project Template", 2 );
		group.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );

		liferayDefaultTemplate =
			SWTUtil.createRadioButton(
				group, "Liferay SDK Default", getPluginImageDescriptor( "/icons/e16/liferay-cube.png" ).createImage(),
				false, 1 );
		Label l = SWTUtil.createLabel( group, SWT.WRAP, "The default template type that comes with Plugins SDK.", 1 );
		l.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
		this.synchHelper.synchCheckbox( liferayDefaultTemplate, TEMPLATE_TYPE_SDK_DEFAULT, null );

		marketplaceTemplate =
			SWTUtil.createRadioButton(
				group, "Liferay Marketplace", getPluginImageDescriptor( "/icons/e16/marketplace.gif" ).createImage(),
				false, 1 );
		l =
			SWTUtil.createLabel(
				group, SWT.WRAP, "Existing Liferay Markplace application will be template for new project.", 1 );
		l.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
		this.synchHelper.synchCheckbox( marketplaceTemplate, TEMPLATE_TYPE_MARKETPLACE, null );


	}

	protected void createProjectTemplateOptionsGroup( Composite parent ) {

		final PageBook pageBook = new PageBook( parent, SWT.NONE );
		GridDataFactory.fillDefaults().grab( true, false ).applyTo( pageBook );

		final Group pluginTypeGroup = createPluginTypeGroup( pageBook );

		final Composite marketplaceGroup = createMarketplaceGroup( pageBook );

		pageBook.showPage( pluginTypeGroup );

		this.synchHelper.getDataModel().addListener( new IDataModelListener() {

			public void propertyChanged( DataModelEvent event ) {
				IDataModel dm = event.getDataModel();

				if ( event.getPropertyName().equals( TEMPLATE_TYPE_MARKETPLACE ) ) {
					if ( dm.getBooleanProperty( TEMPLATE_TYPE_MARKETPLACE ) ) {
						pageBook.showPage( marketplaceGroup );
					}
					else {
						pageBook.showPage( pluginTypeGroup );
					}

				}
			}
		} );
	}

	protected Composite createMarketplaceGroup( Composite parent ) {
		Group group = SWTUtil.createGroup( parent, "Liferay Marketplace Template", 2 );
		group.setLayoutData( new GridData( SWT.FILL, SWT.LEFT, true, true, 2, 1 ) );

		Link chooseLink = SWTUtil.createLink( group, SWT.NONE, "<a>Choose template from marketplace...</a>", 2 );

		final Label imageLabel = SWTUtil.createLabel( group, "", 1 );
		GridDataFactory.fillDefaults().grab( false, false ).span( 1, 2 ).hint( 90, 90 ).applyTo( imageLabel );
		// imageLabel.setBackground( parent.getDisplay().getSystemColor( SWT.COLOR_BLUE ) );

		final Label templateTitleLabel = SWTUtil.createLabel( group, "", 1 );
		GridDataFactory.fillDefaults().grab( true, false ).applyTo( templateTitleLabel );

		final Label wrapLabel = SWTUtil.createWrapLabel( group, "", 1, SWT.DEFAULT );
		GridDataFactory.fillDefaults().grab( true, false ).applyTo( wrapLabel );

		// selectedTemplateLabel.setVisible( false );
		// templateTitleLabel.setVisible( false );
		// imageLabel.setVisible( false );
		// wrapLabel.setVisible( false );

		chooseLink.addSelectionListener( new SelectionAdapter() {

			public void widgetSelected( SelectionEvent e ) {
				CatalogItem selectedItem = openMarketplaceWizard();
				updatePageForItem( templateTitleLabel, imageLabel, wrapLabel, selectedItem );

			}

		} );

		return group;
	}

	protected void updatePageForItem(
		Label templateTitleLabel, Label imageLabel, Label wrapLabel, CatalogItem selectedItem ) {
		templateTitleLabel.setText( selectedItem.getName() );
		try {
			imageLabel.setImage( ImageDescriptor.createFromURL( new URL( selectedItem.getIcon().getImage32() ) ).createImage() );
			imageLabel.setSize( 90, 90 );
			wrapLabel.setText( selectedItem.getDescription() );

			String category = selectedItem.getCategoryId();
			if ( category.equals( "portlet" ) ) {
				getDataModel().setBooleanProperty( PLUGIN_TYPE_PORTLET, true );
				getDataModel().setBooleanProperty( PLUGIN_TYPE_HOOK, false );
				getDataModel().setBooleanProperty( PLUGIN_TYPE_EXT, false );
				getDataModel().setBooleanProperty( PLUGIN_TYPE_LAYOUTTPL, false );
				getDataModel().setBooleanProperty( PLUGIN_TYPE_THEME, false );
			}
			else if ( category.equals( "hook" ) ) {
				getDataModel().setBooleanProperty( PLUGIN_TYPE_PORTLET, false );
				getDataModel().setBooleanProperty( PLUGIN_TYPE_HOOK, true );
				getDataModel().setBooleanProperty( PLUGIN_TYPE_EXT, false );
				getDataModel().setBooleanProperty( PLUGIN_TYPE_LAYOUTTPL, false );
				getDataModel().setBooleanProperty( PLUGIN_TYPE_THEME, false );
			}
			else if ( category.equals( "theme" ) ) {
				getDataModel().setBooleanProperty( PLUGIN_TYPE_PORTLET, false );
				getDataModel().setBooleanProperty( PLUGIN_TYPE_HOOK, false );
				getDataModel().setBooleanProperty( PLUGIN_TYPE_EXT, false );
				getDataModel().setBooleanProperty( PLUGIN_TYPE_LAYOUTTPL, false );
				getDataModel().setBooleanProperty( PLUGIN_TYPE_THEME, true );
			}
			
			App app = (App) selectedItem.getData();
			getDataModel().setProperty( TEMPLATE_FILE_PATH, app.getFilePath() );
		}
		catch ( MalformedURLException e1 ) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}

	protected CatalogItem openMarketplaceWizard() {
		final LRMarketplaceCatalog catalog = new LRMarketplaceCatalog();
		catalog.setEnvironment( DiscoveryCore.createEnvironment() );

		LRMarketplaceCatalogConfiguration configuration = new LRMarketplaceCatalogConfiguration();
		configuration.setVerifyUpdateSiteAvailability( false );
		configuration.setShowCategories( true );
		configuration.setShowInstalled( false );
		configuration.setShowInstalledFilter( false );
		configuration.setShowTagFilter( false );

		final AppTypeFilter typefilter = new AppTypeFilter() {

			@Override
			public void catalogUpdated( boolean wasCancelled ) {
				List<Tag> choices = new ArrayList<Tag>();
				for ( CatalogCategory category : catalog.getCategories() ) {
					if ( category instanceof LRMarketplaceCategory ) {
						LRMarketplaceCategory marketplaceCategory = (LRMarketplaceCategory) category;
						Tag categoryTag =
							new Tag(
								LRMarketplaceCategory.class, marketplaceCategory.getId(), marketplaceCategory.getName() );
						categoryTag.setData( marketplaceCategory );
						choices.add( categoryTag );
					}
				}
				setChoices( choices );
			}
		};
		typefilter.setSelectAllOnNoSelection( true );
		typefilter.setNoSelectionLabel( "All types" );
		typefilter.setTagClassification( Object.class );
		typefilter.setChoices( new ArrayList<Tag>() );

		configuration.getFilters().add( typefilter );

		for ( CatalogFilter filter : configuration.getFilters() ) {
			( (MarketplaceFilter) filter ).setCatalog( catalog );
		}

		LRMarketplaceWizard mpWizard = new LRMarketplaceWizard( catalog, configuration );
		WizardDialog d = new WizardDialog( getShell(), mpWizard );
		d.open();

		CatalogItem item = mpWizard.getSelectedItem();
		return item;
	}

	protected Group createPluginTypeGroup( Composite parent ) {
		Group group = SWTUtil.createGroup(parent, "Plugin Type", 2);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		portletType =
			SWTUtil.createRadioButton(
				group, "Portlet", getPluginImageDescriptor("/icons/e16/portlet.png").createImage(), false, 1);
		Label l = SWTUtil.createLabel(group, SWT.WRAP, "Create a web application using the portlet framework.", 1);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.synchHelper.synchCheckbox(portletType, PLUGIN_TYPE_PORTLET, null);

		hookType =
			SWTUtil.createRadioButton(
				group, "Hook", getPluginImageDescriptor("/icons/e16/hook.png").createImage(), false, 1);
		l = SWTUtil.createLabel(group, SWT.WRAP, "Override or extend Liferay's default behavior and functionality.", 1);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.synchHelper.synchCheckbox(hookType, PLUGIN_TYPE_HOOK, null);

		extType =
			SWTUtil.createRadioButton(
				group, "Ext", getPluginImageDescriptor("/icons/e16/ext.png").createImage(), false, 1);
		l = SWTUtil.createLabel(group, SWT.WRAP, "Light-weight extension environment for Liferay as a plugin.", 1);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.synchHelper.synchCheckbox(extType, PLUGIN_TYPE_EXT, null);

		layoutTemplateType =
			SWTUtil.createRadioButton(
				group, "Layout", getPluginImageDescriptor("/icons/e16/layout.png").createImage(), false, 1);
		l = SWTUtil.createLabel(group, SWT.WRAP, "Create a new custom layout for Liferay pages.", 1);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.synchHelper.synchCheckbox(layoutTemplateType, PLUGIN_TYPE_LAYOUTTPL, null);

		themeType =
			SWTUtil.createRadioButton(
				group, "Theme", getPluginImageDescriptor("/icons/e16/theme.png").createImage(), false, 1);
		l = SWTUtil.createLabel(group, SWT.WRAP, "Build a custom look and feel for the portal.", 1);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.synchHelper.synchCheckbox(themeType, PLUGIN_TYPE_THEME, null);

		return group;
	}

	protected void createProjectGroup(Composite parent) {
		IDataModel nestedProjectDM = model.getNestedModel(NESTED_PROJECT_DM);

		nestedProjectDM.addListener(this);

		projectNameGroup = new NewPluginProjectGroup(parent, getModel(), nestedProjectDM);
	}

	protected void createSDKGroup(Composite parent) {
		Group group = createDefaultGroup(parent, "Liferay Plugin SDK", 2);
		((GridData) group.getLayoutData()).grabExcessVerticalSpace = false;

		// Composite labelContainer = new Composite(group, SWT.NONE);

		// GridData gd = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);

		// labelContainer.setLayoutData(gd);
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = 3;
		gl.marginWidth = 0;

		// labelContainer.setLayout(gl);
		// Label label = SWTUtil.createLabel(labelContainer,
		// "Use Liferay SDK: ", 1);
		// label.setLayoutData(gd);

		// IDataModel nestedProjectModel =
		// getModel().getNestedModel(NESTED_PROJECT_DM);
		// nestedProjectModel.addListener(this);
		// modelHelper = new DataModelSynchHelper(nestedProjectModel);
		// Collection props = getModel().getAllProperties();
		// for (Object prop : props) {
		// System.out.println(prop);
		// }

		getModel().addListener(this);

		Combo sdkCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		sdkCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Link configureSDKsLink = new Link(group, SWT.UNDERLINE_LINK);
		configureSDKsLink.setText("<a href=\"#\">Configure SDKs</a>");
		configureSDKsLink.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		configureSDKsLink.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				configureSDKsLinkSelected(e);
			}
		});
		this.synchHelper.synchCombo(sdkCombo, LIFERAY_SDK_NAME, new Control[] {
			configureSDKsLink
		});

	}

	protected void createServerTargetComposite(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		((GridData) group.getLayoutData()).grabExcessVerticalSpace = false;
		group.setText("Liferay runtime");
		group.setLayoutData(gdhfill());
		group.setLayout(new GridLayout(2, false));

		serverTargetCombo = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
		serverTargetCombo.setLayoutData(gdhfill());

		Button newServerTargetButton = new Button(group, SWT.NONE);
		newServerTargetButton.setText("New ...");
		newServerTargetButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				final DataModelPropertyDescriptor[] preAdditionDescriptors =
					model.getValidPropertyDescriptors(FACET_RUNTIME);

				boolean isOK = ServerUIUtil.showNewRuntimeWizard(getShell(), getModuleTypeID(), null, "com.liferay.");

				if (isOK) {
					DataModelPropertyDescriptor[] postAdditionDescriptors =
						model.getValidPropertyDescriptors(FACET_RUNTIME);

					Object[] preAddition = new Object[preAdditionDescriptors.length];

					for (int i = 0; i < preAddition.length; i++) {
						preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
					}

					Object[] postAddition = new Object[postAdditionDescriptors.length];

					for (int i = 0; i < postAddition.length; i++) {
						postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
					}

					Object newAddition = CoreUtil.getNewObject(preAddition, postAddition);

					if (newAddition != null) // can this ever be null?
						model.setProperty(FACET_RUNTIME, newAddition);
				}
			}
		});

		Control[] deps = new Control[] {
			newServerTargetButton
		};
		synchHelper.synchCombo(serverTargetCombo, FACET_RUNTIME, deps);
		if (serverTargetCombo.getSelectionIndex() == -1 && serverTargetCombo.getVisibleItemCount() != 0) {
			serverTargetCombo.select(0);
		}
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(GridData.FILL_BOTH));

		createProjectGroup(top);
		createImportProjectLink(top);
		createLiferayRuntimeGroup(top);

		// createSDKGroup(top);
		// createServerTargetComposite(top);
		// createPresetPanel(top);

		createProjectTemplateGroup( top );

		createProjectTemplateOptionsGroup( top );


		createWorkingSetGroupPanel(top, new String[] {
			RESOURCE_WORKING_SET, JAVA_WORKING_SET
		});

		updateControls();

		return top;
	}

	@Override
	protected void enter() {
		super.enter();

		String projectType = ((NewPluginProjectWizard) getWizard()).getProjectType();

		if (CoreUtil.isNullOrEmpty(projectType)) {
			projectType = IPluginFacetConstants.LIFERAY_PORTLET_FACET_ID;
		}

		setShouldValidatePage(false);
		if (IPluginFacetConstants.LIFERAY_PORTLET_FACET_ID.equals(projectType.toLowerCase())) {
			getDataModel().setProperty(PLUGIN_TYPE_PORTLET, true);
			getDataModel().setProperty(PLUGIN_TYPE_HOOK, false);
			getDataModel().setProperty(PLUGIN_TYPE_EXT, false);
			getDataModel().setProperty(PLUGIN_TYPE_LAYOUTTPL, false);
			getDataModel().setProperty(PLUGIN_TYPE_THEME, false);
		}
		else if (IPluginFacetConstants.LIFERAY_HOOK_FACET_ID.equals(projectType.toLowerCase())) {
			getDataModel().setProperty(PLUGIN_TYPE_HOOK, true);
			getDataModel().setProperty(PLUGIN_TYPE_PORTLET, false);
			getDataModel().setProperty(PLUGIN_TYPE_EXT, false);
			getDataModel().setProperty(PLUGIN_TYPE_LAYOUTTPL, false);
			getDataModel().setProperty(PLUGIN_TYPE_THEME, false);
		}
		else if (IPluginFacetConstants.LIFERAY_EXT_FACET_ID.equals(projectType.toLowerCase())) {
			getDataModel().setProperty(PLUGIN_TYPE_EXT, true);
			getDataModel().setProperty(PLUGIN_TYPE_PORTLET, false);
			getDataModel().setProperty(PLUGIN_TYPE_HOOK, false);
			getDataModel().setProperty(PLUGIN_TYPE_LAYOUTTPL, false);
			getDataModel().setProperty(PLUGIN_TYPE_THEME, false);
		}
		else if (IPluginFacetConstants.LIFERAY_LAYOUTTPL_FACET_ID.equals(projectType.toLowerCase())) {
			getDataModel().setProperty(PLUGIN_TYPE_LAYOUTTPL, true);
			getDataModel().setProperty(PLUGIN_TYPE_PORTLET, false);
			getDataModel().setProperty(PLUGIN_TYPE_HOOK, false);
			getDataModel().setProperty(PLUGIN_TYPE_EXT, false);
			getDataModel().setProperty(PLUGIN_TYPE_THEME, false);
		}
		else if (IPluginFacetConstants.LIFERAY_THEME_FACET_ID.equals(projectType.toLowerCase())) {
			getDataModel().setProperty(PLUGIN_TYPE_THEME, true);
			getDataModel().setProperty(PLUGIN_TYPE_LAYOUTTPL, false);
			getDataModel().setProperty(PLUGIN_TYPE_PORTLET, false);
			getDataModel().setProperty(PLUGIN_TYPE_HOOK, false);
			getDataModel().setProperty(PLUGIN_TYPE_EXT, false);
		}
		setShouldValidatePage(true);
		// getDataModel().setProperty(PLUGIN_TYPE_PORTLET, true);
	}

	protected IDataModel getModel() {
		return model;
	}

	protected ImageDescriptor getPluginImageDescriptor(String path) {
		return ImageDescriptor.createFromURL(ProjectUIPlugin.getDefault().getBundle().getEntry(path));
	}

	@Override
	protected String[] getValidationPropertyNames() {
		String[] superProperties = super.getValidationPropertyNames();

		List<String> list = Arrays.asList(superProperties);

		ArrayList<String> arrayList = new ArrayList<String>();

		arrayList.addAll(list);

		arrayList.add(LIFERAY_SDK_NAME);
		arrayList.add(FACET_RUNTIME);
		arrayList.add(PROJECT_NAME);
		arrayList.add(DISPLAY_NAME);
		arrayList.add(FACET_PROJECT_NAME);
		arrayList.add(PLUGIN_TYPE_PORTLET);
		arrayList.add(PLUGIN_TYPE_HOOK);
		arrayList.add(PLUGIN_TYPE_EXT);
		arrayList.add(PLUGIN_TYPE_THEME);
		arrayList.add(PLUGIN_TYPE_LAYOUTTPL);

		return (String[]) arrayList.toArray(new String[0]);
	}

	@Override
	protected void validatePage(boolean showMessage) {
		if (shouldValidatePage) {
			super.validatePage(showMessage);
		}
	}

	// protected void createSimpleProjectGroup(Composite parent) {
	// IDataModel nestedProjectDM =
	// getDataModel().getNestedModel(NESTED_PROJECT_DM);
	// nestedProjectDM.addListener(this);
	// this.simpleProjectGroup = new NewSimpleProjectGroup(parent,
	// nestedProjectDM);
	// }

}
