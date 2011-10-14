package com.liferay.ide.eclipse.project.ui.wizard;

import java.util.List;

import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogCategory;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.equinox.internal.p2.discovery.model.Certification;
import org.eclipse.equinox.internal.p2.discovery.model.Tag;


@SuppressWarnings( "restriction" )
public class LRMarketplaceCatalog extends Catalog {

	protected void update(
		List<CatalogCategory> newCategories, List<CatalogItem> newItems, List<Certification> newCertifications,
		List<Tag> newTags ) {
		super.update( newCategories, newItems, newCertifications, newTags );

		getFilteredItems().addAll( newItems );

	}
}
