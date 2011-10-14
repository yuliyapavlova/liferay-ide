package com.liferay.ide.eclipse.project.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogCategory;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogFilter;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogViewer;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.statushandlers.StatusManager;


@SuppressWarnings( "restriction" )
public class LRMarketplaceViewer extends CatalogViewer {

	public static class MarketplaceCatalogContentProvider extends CatalogContentProvider {

		private static final Object[] NO_ELEMENTS = new Object[0];

		@Override
		public Catalog getCatalog() {
			return super.getCatalog();
		}

		@Override
		public Object[] getElements( Object inputElement ) {
			if ( getCatalog() != null ) {
				// don't provide any categories unless it's featured
				List<Object> items = new ArrayList<Object>( getCatalog().getItems() );
				for ( CatalogCategory category : getCatalog().getCategories() ) {
					if ( category instanceof LRMarketplaceCategory ) {
						LRMarketplaceCategory marketplaceCategory = (LRMarketplaceCategory) category;
						items.add( 0, category );
					}
				}
				return items.toArray();
			}
			return NO_ELEMENTS;
		}

	}

	private ViewerFilter[] filters;
	private LRMarketplaceWizard wizard;

	public LRMarketplaceViewer( Catalog catalog, IShellProvider shellProvider, LRMarketplaceWizard wizard ) {
		super( catalog, shellProvider, wizard.getContainer(), wizard.getConfiguration() );
		setAutomaticFind( false );
		this.wizard = wizard;
	}

	@Override
	protected void doCreateHeaderControls( Composite parent ) {
		final int originalChildCount = parent.getChildren().length;
		for ( CatalogFilter filter : getConfiguration().getFilters() ) {
			if ( filter instanceof MarketplaceFilter ) {
				MarketplaceFilter marketplaceFilter = (MarketplaceFilter) filter;
				marketplaceFilter.createControl( parent );
			}
		}
		Control[] children = parent.getChildren();
		for ( int x = originalChildCount; x < children.length; ++x ) {
			Control child = children[x];
			GridDataFactory.swtDefaults().hint( 135, SWT.DEFAULT ).applyTo( child );
		}

	}

	private LRMarketplaceWizard getWizard() {
		return wizard;
	}

	@Override
	public void updateCatalog() {

			super.updateCatalog();
		refresh();
	}

	@Override
	protected CatalogContentProvider doCreateContentProvider() {
		return new MarketplaceCatalogContentProvider();
	}

	@Override
	protected void filterTextChanged() {
		doFind( getFilterText() );
	}


}
