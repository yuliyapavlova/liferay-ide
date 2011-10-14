package com.liferay.ide.eclipse.project.ui.wizard;

import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.equinox.internal.p2.discovery.AbstractDiscoveryStrategy;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.equinox.internal.p2.discovery.model.Icon;
import org.eclipse.equinox.internal.p2.discovery.model.Overview;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


@SuppressWarnings( "restriction" )
public class LRMarketplaceDiscoveryStrategy extends AbstractDiscoveryStrategy {

	private static final Pattern BREAK_PATTERN = Pattern.compile( "<!--\\s*break\\s*-->" ); //$NON-NLS-1$

	private MarketplaceCatalogSource source = new MarketplaceCatalogSource();

	@Override
	public void performDiscovery( IProgressMonitor monitor ) throws CoreException {

		List<App> apps = new ArrayList<App>();
		
		try {
			URL baseUrl = FileLocator.toFileURL( ProjectUIPlugin.getDefault().getBundle().getEntry( "/MP" ) );
			File dataFile =
				new File( FileLocator.toFileURL(
					ProjectUIPlugin.getDefault().getBundle().getEntry( "/MP/data/apps.xml" ) ).getFile() );

			Document appsData = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( dataFile );

			NodeList appElements = appsData.getElementsByTagName( "app" );

			for ( int i = 0; i < appElements.getLength(); i++ ) {
				App app = new App();
				Element appElement = (Element) appElements.item( i );

				String title = appElement.getElementsByTagName( "title" ).item( 0 ).getTextContent().trim();
				app.setId( title );
				app.setName( title );

				String subTitle = appElement.getElementsByTagName( "subTitle" ).item( 0 ).getTextContent().trim();
				app.setShortDescription( subTitle );

				String description = appElement.getElementsByTagName( "description" ).item( 0 ).getTextContent().trim();
				app.setBody( description );

				String supportedBy = appElement.getElementsByTagName( "supportedBy" ).item( 0 ).getTextContent().trim();
				app.setCompanyName( supportedBy );

				String website = appElement.getElementsByTagName( "website" ).item( 0 ).getTextContent().trim();
				app.setUrl( website );

				String category = appElement.getElementsByTagName( "category" ).item( 0 ).getTextContent().trim();
				app.setCategory( category );

				String fileName = appElement.getElementsByTagName( "fileName" ).item( 0 ).getTextContent().trim();
				app.setFilePath( new File( baseUrl.getFile() + "/wars/" + fileName ).toURI().toURL().toString() );

				String iconFileName =
					appElement.getElementsByTagName( "iconFileName" ).item( 0 ).getTextContent().trim();
				app.setImage( new File( baseUrl.getFile() + "/icons/" + iconFileName ).toURI().toURL().toString() );

				String screenshot =
					appElement.getElementsByTagName( "screenShotFileName" ).item( 0 ).getTextContent().trim();
				app.setScreenshot( new File( baseUrl.getFile() + "/screen-shots/" + screenshot ).toURI().toURL().toString() );

				apps.add( app );
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

		ConcurrentTaskManager executor = new ConcurrentTaskManager( apps.size(), "Loading..." );

		LRMarketplaceCategory portlet = new LRMarketplaceCategory();
		portlet.setId( "portlet" );
		portlet.setName( "Portlets" );
		portlet.setSource( source );
		categories.add( portlet );

		LRMarketplaceCategory theme = new LRMarketplaceCategory();
		theme.setId( "theme" );
		theme.setName( "Themes" );
		theme.setSource( source );
		categories.add( theme );

		LRMarketplaceCategory hook = new LRMarketplaceCategory();
		hook.setId( "hook" );
		hook.setName( "Hooks" );
		hook.setSource( source );
		categories.add( hook );

		for ( App app : apps ) {
			addCatalogItem( app, executor, monitor );
		}

	}

	private void addCatalogItem( final App app, ConcurrentTaskManager executor, IProgressMonitor monitor ) {
		final MarketplaceCatalogItem catalogItem = new MarketplaceCatalogItem();
		catalogItem.setId( app.getId() );
		catalogItem.setName( app.getName() );
		catalogItem.setCategoryId( app.getCategory() );
		catalogItem.setData( app );
		catalogItem.setInstalled( false );
		catalogItem.setSource( source );
		catalogItem.setLicense( app.getLicense() );

		if ( app.getShortDescription() == null && app.getBody() != null ) {
			// bug 306653 <!--break--> marks the end of the short description.
			String descriptionText = app.getBody();
			Matcher matcher = BREAK_PATTERN.matcher( app.getBody() );
			if ( matcher.find() ) {
				int start = matcher.start();
				if ( start > 0 ) {
					String shortDescriptionText = descriptionText.substring( 0, start ).trim();
					if ( shortDescriptionText.length() > 0 ) {
						descriptionText = shortDescriptionText;
					}
				}
			}
			catalogItem.setDescription( descriptionText );
		}
		else {
			catalogItem.setDescription( app.getShortDescription() );
		}
		catalogItem.setProvider( app.getCompanyName() );
		if ( app.getBody() != null || app.getScreenshot() != null ) {
			final Overview overview = new Overview();
			overview.setItem( catalogItem );
			overview.setSummary( app.getBody() );
			overview.setUrl( app.getUrl() );
			catalogItem.setOverview( overview );

			if ( app.getScreenshot() != null ) {
				overview.setScreenshot( app.getScreenshot() );
			}
		}
		if ( app.getImage() != null ) {
				createIcon( catalogItem, app );
		}
		items.add( catalogItem );

		try {
			executor.waitUntilFinished( new SubProgressMonitor( monitor, 0 ) );
		}
		catch ( CoreException e ) {
			// just log, since this is expected to occur frequently

		}

		executor.shutdownNow();
	}

	private void createIcon( CatalogItem catalogItem, final App node ) {
		Icon icon = new Icon();
		// don't know the size
		icon.setImage32( node.getImage() );
		icon.setImage48( node.getImage() );
		icon.setImage64( node.getImage() );
		catalogItem.setIcon( icon );
	}

}
