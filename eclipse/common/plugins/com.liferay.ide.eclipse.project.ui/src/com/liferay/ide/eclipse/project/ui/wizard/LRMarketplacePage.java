package com.liferay.ide.eclipse.project.ui.wizard;

import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogPage;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


@SuppressWarnings( "restriction" )
public class LRMarketplacePage extends CatalogPage {

	private final LRMarketplaceCatalogConfiguration configuration;

	private boolean updated;

	public LRMarketplacePage( LRMarketplaceCatalog catalog, LRMarketplaceCatalogConfiguration configuration ) {
		super( catalog );
		this.configuration = configuration;
		setDescription( "select template" );
		setTitle( "Liferay marketplace" );
	}

	@Override
	protected CatalogViewer doCreateViewer( Composite parent ) {
		LRMarketplaceViewer viewer = new LRMarketplaceViewer( getCatalog(), this, getWizard() );
		viewer.setMinimumHeight( MINIMUM_HEIGHT );
		viewer.createControl( parent );
		return viewer;
	}

	@Override
	public LRMarketplaceWizard getWizard() {
		return (LRMarketplaceWizard) super.getWizard();
	}

	@Override
	protected LRMarketplaceViewer getViewer() {
		return (LRMarketplaceViewer) super.getViewer();
	}

	@Override
	protected void doUpdateCatalog() {
		if ( !updated ) {
			updated = true;
			Display.getCurrent().asyncExec( new Runnable() {

				public void run() {
					if ( !getControl().isDisposed() && isCurrentPage() ) {
						getViewer().updateCatalog();
					}
				}
			} );
		}
	}

	@Override
	public void setVisible( boolean visible ) {
		if ( visible ) {

			getWizard().initializeCatalog();
			updated = false;

		}
		super.setVisible( visible );
	}

}
