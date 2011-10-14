package com.liferay.ide.eclipse.project.ui.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.equinox.internal.p2.discovery.AbstractDiscoveryStrategy;
import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogConfiguration;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.DiscoveryWizard;


@SuppressWarnings( "restriction" )
public class LRMarketplaceWizard extends DiscoveryWizard {

	private boolean initialSelectionInitialized;
	private LRMarketplacePage page;

	public LRMarketplaceWizard( Catalog catalog, CatalogConfiguration configuration ) {
		super( catalog, configuration );
		setWindowTitle( "Liferay Marketplace" );
	}

	@Override
	public LRMarketplaceCatalogConfiguration getConfiguration() {
		return (LRMarketplaceCatalogConfiguration) super.getConfiguration();
	}

	@Override
	public LRMarketplaceCatalog getCatalog() {
		return (LRMarketplaceCatalog) super.getCatalog();
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	protected LRMarketplacePage doCreateCatalogPage() {
		page = new LRMarketplacePage( getCatalog(), getConfiguration() );
		return page;
	}

	void initializeInitialSelection() throws CoreException {
		initializeCatalog();
	}

	boolean wantInitializeInitialSelection() {
		return !initialSelectionInitialized;
	}

	void initializeCatalog() {
		for ( AbstractDiscoveryStrategy strategy : getCatalog().getDiscoveryStrategies() ) {
			strategy.dispose();
		}
		getCatalog().getDiscoveryStrategies().clear();
		getCatalog().getDiscoveryStrategies().add( new LRMarketplaceDiscoveryStrategy() );
	}

	public CatalogItem getSelectedItem() {
		return page.getViewer().getCheckedItems().get( 0 );
	}

}
