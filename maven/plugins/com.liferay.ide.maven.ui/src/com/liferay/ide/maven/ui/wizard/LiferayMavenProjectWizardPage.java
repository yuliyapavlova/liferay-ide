package com.liferay.ide.maven.ui.wizard;

import com.liferay.ide.maven.ui.LiferayMavenUI;
import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.m2e.core.archetype.ArchetypeUtil;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.archetype.ArchetypeCatalogFactory;
import org.eclipse.m2e.core.internal.archetype.ArchetypeManager;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.ui.internal.Messages;
import org.eclipse.m2e.core.ui.internal.util.M2EUIUtils;
import org.eclipse.m2e.core.ui.internal.wizards.AbstractMavenWizardPage;
import org.eclipse.m2e.core.ui.internal.wizards.MavenProjectWizardArchetypePage;
import org.eclipse.m2e.core.ui.internal.wizards.WorkingSetGroup;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayMavenProjectWizardPage extends AbstractMavenWizardPage
{

    private IPath location;
    private WorkingSetGroup workingSetGroup;
    private List<IWorkingSet> workingSets;
    private Text projectName;
    private Button useDefaultWorkspaceLocationButton;
    private Label locationLabel;
    private Combo locationCombo;
    private TableViewer viewer;
    private Text descriptionText;
    private Button showLastVersionButton;
    private Button includeShapshotsButton;
    private Map<String, List<String>> archetypeVersions;
    private ArchetypeCatalogFactory catalogFactory = null;

    /** the list of available archetypes */
    volatile Collection<Archetype> archetypes;

    public LiferayMavenProjectWizardPage( ProjectImportConfiguration importConfiguration, List<IWorkingSet> workingSets )
    {
        super( "LiferayMavenProjectWizardPage", importConfiguration ); //$NON-NLS-1$

        setTitle( Msgs.newLiferayProject );
        setDescription( Msgs.newLiferayProjectDesc );

        this.workingSets = workingSets;
    }

    public void createControl( Composite parent )
    {
        Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout(3, false));

        final Label label = new Label( container, SWT.NONE );
        final GridData labelData = new GridData( SWT.FILL, SWT.FILL, false, false, 1, 1 );
        label.setLayoutData( labelData );
        label.setText( Msgs.projectName );

        projectName = new Text( container, SWT.BORDER );
        projectName.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 2, 1 ) );
        projectName.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    validate();
                }
            }
        );

        SWTUtil.createHorizontalSpacer( container, 3 );

        useDefaultWorkspaceLocationButton = new Button( container, SWT.CHECK );
        GridData useDefaultWorkspaceLocationButtonData = new GridData( SWT.LEFT, SWT.CENTER, false, false, 3, 1 );
        useDefaultWorkspaceLocationButton.setLayoutData( useDefaultWorkspaceLocationButtonData );
        useDefaultWorkspaceLocationButton.setText( Msgs.useDefaultWorkspaceLocation );
        useDefaultWorkspaceLocationButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    boolean inWorkspace = isInWorkspace();

                    locationLabel.setEnabled( !inWorkspace );
                    locationCombo.setEnabled( !inWorkspace );
                }
            }
        );
        useDefaultWorkspaceLocationButton.setSelection( true );

        locationLabel = new Label( container, SWT.NONE );
        GridData locationLabelData = new GridData();
        locationLabelData.horizontalIndent = 10;
        locationLabel.setLayoutData( locationLabelData );
        locationLabel.setText( Msgs.location );
        locationLabel.setEnabled( false );

        locationCombo = new Combo( container, SWT.NONE );
        GridData locationComboData = new GridData( SWT.FILL, SWT.CENTER, true, false );
        locationCombo.setLayoutData( locationComboData );
        locationCombo.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    validate();
                }
            }
        );
        locationCombo.setEnabled( false );
        addFieldWithHistory( "location", locationCombo ); //$NON-NLS-1$

        Button locationBrowseButton = new Button( container, SWT.NONE );
        GridData locationBrowseButtonData = new GridData( SWT.FILL, SWT.CENTER, false, false );
        locationBrowseButton.setLayoutData( locationBrowseButtonData );
        locationBrowseButton.setText( Msgs.browse );
        locationBrowseButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    DirectoryDialog dialog = new DirectoryDialog( getShell() );
                    dialog.setText( Msgs.selectLocation );

                    String path = locationCombo.getText();

                    if( path.length() == 0 )
                    {
                        path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
                    }

                    dialog.setFilterPath( path );

                    String selectedDir = dialog.open();

                    if( selectedDir != null )
                    {
                        locationCombo.setText( selectedDir );
                        useDefaultWorkspaceLocationButton.setSelection( false );
                        validate();
                    }
                }
            }
        );

        createArchetypeViewer( container );

        this.workingSetGroup = new WorkingSetGroup( container, workingSets, getShell() );

        if( location == null || Platform.getLocation().equals( location ) )
        {
            // useDefaultWorkspaceLocationButton.setSelection(true);
        }
        else
        {
            // useDefaultWorkspaceLocationButton.setSelection(false);
            // locationLabel.setEnabled(true);
            // locationCombo.setEnabled(true);
            locationCombo.setText( location.toOSString() );
        }

//        createAdvancedSettings(container, new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));

        reloadViewer();

        setControl(container);
    }

    private void createArchetypeViewer( Composite container )
    {
        VersionsFilter versionFilter = new VersionsFilter( true, false );

        final Composite tableComposite = SWTUtil.createComposite( container, 2, 3, SWT.FILL );

        viewer = new TableViewer( tableComposite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION );
        Table table = viewer.getTable();
        table.setData( "name", "archetypesTable" ); //$NON-NLS-1$ //$NON-NLS-2$
        table.setHeaderVisible( true );

        TableColumn column0 = new TableColumn( table, SWT.LEFT );
        column0.setWidth( 150 );
        column0.setText( Msgs.archetype );

        TableColumn column2 = new TableColumn( table, SWT.LEFT );
        column2.setWidth( 100 );
        column2.setText( Msgs.version );

        GridData tableData = new GridData( SWT.FILL, SWT.FILL, true, true );
        tableData.widthHint = 400;
        tableData.heightHint = 200;
        tableData.horizontalSpan = 2;
        table.setLayoutData( tableData );

        viewer.setLabelProvider( new ArchetypeLabelProvider() );

        viewer.setComparator
        (
            new ViewerComparator()
            {
                public int compare( Viewer viewer, Object o1, Object o2 )
                {
                    final Archetype e1 = (Archetype) o1;
                    final Archetype e2 = (Archetype) o2;

                    return MavenProjectWizardArchetypePage.ARCHETYPE_COMPARATOR.compare( e1, e2 );
                }
            }
        );

        viewer.setComparer
        (
            new IElementComparer()
            {
                public int hashCode( Object obj )
                {
                    if( obj instanceof Archetype )
                    {
                        return ArchetypeUtil.getHashCode( (Archetype) obj );
                    }

                    return obj.hashCode();
                }

                public boolean equals( Object one, Object another )
                {
                    if( one instanceof Archetype && another instanceof Archetype )
                    {
                        return ArchetypeUtil.areEqual( (Archetype) one, (Archetype) another );
                    }

                    return one.equals( another );
                }
            }
        );

        viewer.setFilters( new ViewerFilter[] { versionFilter } );

        viewer.setContentProvider
        (
            new IStructuredContentProvider()
            {
                public Object[] getElements( Object inputElement )
                {
                    if( inputElement instanceof Collection )
                    {
                        return ( (Collection<?>) inputElement ).toArray();
                    }

                    return new Object[0];
                }

                public void dispose()
                {
                }

                public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
                {
                }
            }
        );

        viewer.addSelectionChangedListener
        (
            new ISelectionChangedListener()
            {
                public void selectionChanged( SelectionChangedEvent event )
                {
                    Archetype archetype = getArchetype();

                    if( archetype != null )
                    {
                        String repositoryUrl = archetype.getRepository();
                        String description = archetype.getDescription();

                        String text = description == null ? "" : description; //$NON-NLS-1$
                        text = text.replaceAll( "\n", "" ).replaceAll( "\\s{2,}", " " ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

                        if( repositoryUrl != null )
                        {
                            text += text.length() > 0 ? "\n" + repositoryUrl : repositoryUrl; //$NON-NLS-1$
                        }

                        descriptionText.setText( text );
                        setPageComplete( true );
                    }
                    else
                    {
                        descriptionText.setText( "" ); //$NON-NLS-1$
                        setPageComplete( false );
                    }
                }
            }
        );

        viewer.addOpenListener
        (
            new IOpenListener()
            {
                public void open( OpenEvent openevent )
                {
                    if( canFlipToNextPage() )
                    {
                        getContainer().showPage( getNextPage() );
                    }
                }
            }
        );

        Composite composite2 = new Composite( tableComposite, SWT.NONE );
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.marginHeight = 0;
        gridLayout2.marginWidth = 0;
        gridLayout2.horizontalSpacing = 0;
        composite2.setLayout( gridLayout2 );

        descriptionText = new Text( composite2, SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY | SWT.MULTI | SWT.BORDER );

        GridData descriptionTextData = new GridData( SWT.FILL, SWT.FILL, true, true );
        descriptionTextData.heightHint = 40;
        descriptionText.setLayoutData( descriptionTextData );
        // whole dialog resizes badly without the width hint to the desc text
        descriptionTextData.widthHint = 250;

        Composite buttonComposite = new Composite( container, SWT.NONE );
        GridData gd_buttonComposite = new GridData( SWT.FILL, SWT.CENTER, false, false, 3, 1 );
        buttonComposite.setLayoutData( gd_buttonComposite );
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.numColumns = 3;
        buttonComposite.setLayout( gridLayout );

        showLastVersionButton = new Button( buttonComposite, SWT.CHECK );
        showLastVersionButton.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, true, false ) );
        showLastVersionButton.setText( Msgs.showLastVersion );
        showLastVersionButton.setSelection( true );
        showLastVersionButton.addSelectionListener( versionFilter );

        includeShapshotsButton = new Button( buttonComposite, SWT.CHECK );
        GridData buttonData = new GridData( SWT.LEFT, SWT.CENTER, true, false );
        buttonData.horizontalIndent = 25;
        includeShapshotsButton.setLayoutData( buttonData );
        includeShapshotsButton.setText( Msgs.includeSnapshot );
        includeShapshotsButton.setSelection( false );
        includeShapshotsButton.addSelectionListener( versionFilter );
    }

    public void addArchetypeSelectionListener( ISelectionChangedListener listener )
    {
        viewer.addSelectionChangedListener( listener );
    }

    public List<Archetype> getArchetypesForCatalog()
    {
        if( catalogFactory == null )
        {
            return getAllArchetypes();
        }
        try
        {
            return catalogFactory.getArchetypeCatalog().getArchetypes();

        }
        catch( CoreException ce )
        {
            setErrorMessage( org.eclipse.m2e.core.ui.internal.Messages.MavenProjectWizardArchetypePage_error_read );
            return null;
        }
    }

    private List<Archetype> getAllArchetypes()
    {
        ArchetypeManager manager = MavenPluginActivator.getDefault().getArchetypeManager();
        Collection<ArchetypeCatalogFactory> archetypeCatalogs = manager.getArchetypeCatalogs();
        ArrayList<Archetype> list = new ArrayList<Archetype>();

        for( ArchetypeCatalogFactory catalog : archetypeCatalogs )
        {
            try
            {
                // temporary hack to get around 'Test Remote Catalog' blowing up on download
                // described in https://issues.sonatype.org/browse/MNGECLIPSE-1792
                if( catalog.getDescription().startsWith( "Test" ) ) { //$NON-NLS-1$
                    continue;
                }
                @SuppressWarnings( "rawtypes" )
                List arcs = catalog.getArchetypeCatalog().getArchetypes();
                if( arcs != null )
                {
                    list.addAll( arcs );
                }
            }
            catch( Exception ce )
            {
                LiferayMavenUI.logError( "Unable to read archetype catalog: " + catalog.getId(), ce ); //$NON-NLS-1$
            }
        }
        return list;
    }

    protected void reloadViewer()
    {
        Display.getDefault().asyncExec
        (
            new Runnable()
            {
                public void run()
                {
                    if( isCurrentPage() )
                    {
                        StructuredSelection sel = (StructuredSelection) viewer.getSelection();
                        Archetype selArchetype = null;

                        if( sel != null && sel.getFirstElement() != null )
                        {
                            selArchetype = (Archetype) sel.getFirstElement();
                        }

                        if( selArchetype != null )
                        {
                            loadArchetypes(
                                selArchetype.getGroupId(), selArchetype.getArtifactId(), selArchetype.getVersion() );
                        }
                        else
                        {
                            loadArchetypes( "org.apache.maven.archetypes", "maven-archetype-quickstart", "1.0" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        }
                    }
                }
            }
        );
    }

    void loadArchetypes( final String groupId, final String artifactId, final String version )
    {
        Job job = new Job( Messages.wizardProjectPageArchetypeRetrievingArchetypes )
        {
            protected IStatus run( IProgressMonitor monitor )
            {
                try
                {
                    List<Archetype> catalogArchetypes = getArchetypesForCatalog();

                    if( catalogArchetypes == null || catalogArchetypes.size() == 0 )
                    {
                        Display.getDefault().asyncExec( new Runnable()
                        {
                            public void run()
                            {
                                if( catalogFactory != null && "Nexus Indexer".equals( catalogFactory.getDescription() ) ) { //$NON-NLS-1$
                                    setErrorMessage( org.eclipse.m2e.core.ui.internal.Messages.MavenProjectWizardArchetypePage_error_no );
                                }
                            }
                        } );
                    }
                    else
                    {
                        Display.getDefault().asyncExec( new Runnable()
                        {
                            public void run()
                            {
                                setErrorMessage( null );
                            }
                        } );
                    }

                    if( catalogArchetypes == null )
                    {
                        return Status.CANCEL_STATUS;
                    }

                    TreeSet<Archetype> archs =
                        new TreeSet<Archetype>( MavenProjectWizardArchetypePage.ARCHETYPE_COMPARATOR );
                    archs.addAll( catalogArchetypes );
                    archetypes = archs;

                    Display.getDefault().asyncExec( new Runnable()
                    {
                        public void run()
                        {
                            updateViewer( groupId, artifactId, version );
                        }
                    } );
                }
                catch( Exception e )
                {
                    monitor.done();
                    return Status.CANCEL_STATUS;
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();
    }


    public void setVisible( boolean visible )
    {
        super.setVisible( visible );

        if( visible )
        {
            ArchetypeManager archetypeManager = MavenPluginActivator.getDefault().getArchetypeManager();
            catalogFactory = archetypeManager.getArchetypeCatalogFactory( "All Catalogs" ); //$NON-NLS-1$

            viewer.getTable().setFocus();
            Archetype selected = getArchetype();

            if( selected != null )
            {
                viewer.reveal( selected );
            }
        }
    }

    private static Map<String, List<String>> getArchetypeVersions( Collection<Archetype> archetypes )
    {
        final HashMap<String, List<String>> archetypeVersions = new HashMap<String, List<String>>();

        for( Archetype currentArchetype : archetypes )
        {
            final String version = currentArchetype.getVersion();

            if( M2EUIUtils.nullOrEmpty( version ) )
            {
                // we don't know how to handle null/empty versions
                continue;
            }

            final String key = getArchetypeKey( currentArchetype );

            List<String> versions = archetypeVersions.get( key );

            if( versions == null )
            {
                versions = new ArrayList<String>();
                archetypeVersions.put( key, versions );
            }

            versions.add( version );
        }

        Comparator<String> comparator = new Comparator<String>()
        {
            public int compare( String s1, String s2 )
            {
                DefaultArtifactVersion v1 = new DefaultArtifactVersion( s1 );
                DefaultArtifactVersion v2 = new DefaultArtifactVersion( s2 );

                return v2.compareTo( v1 );
            }
        };

        for( List<String> versions : archetypeVersions.values() )
        {
            Collections.sort( versions, comparator );
        }

        return archetypeVersions;
    }

    protected void selectArchetype( String groupId, String artifactId, String version )
    {
        Archetype archetype = findArchetype( groupId, artifactId, version );

        Table table = viewer.getTable();

        if( archetype != null )
        {
            viewer.setSelection( new StructuredSelection( archetype ), true );

            int n = table.getSelectionIndex();
            table.setSelection( n );
        }
    }

    protected Archetype findArchetype( String groupId, String artifactId, String version )
    {
        for( Archetype archetype : archetypes )
        {
            if( archetype.getGroupId().equals( groupId ) && archetype.getArtifactId().equals( artifactId ) )
            {
                if( version == null || version.equals( archetype.getVersion() ) )
                {
                    return archetype;
                }
            }
        }

        return version == null ? null : findArchetype( groupId, artifactId, null );
    }

    void updateViewer( String groupId, String artifactId, String version )
    {
        archetypeVersions = getArchetypeVersions( archetypes );

        viewer.setInput( archetypes );

        selectArchetype( groupId, artifactId, version );

        Table table = viewer.getTable();
        int columnCount = table.getColumnCount();
        int width = 0;

        for( int i = 0; i < columnCount; i++ )
        {
            TableColumn column = table.getColumn( i );
            column.pack();
            width += column.getWidth();
        }

        GridData tableData = (GridData) table.getLayoutData();
        int oldHint = tableData.widthHint;

        if( width > oldHint )
        {
            tableData.widthHint = width;
        }

        getShell().pack( true );
        tableData.widthHint = oldHint;
    }

    public Archetype getArchetype()
    {
        return (Archetype) ( (IStructuredSelection) viewer.getSelection() ).getFirstElement();
    }

    public boolean isInWorkspace()
    {
        return useDefaultWorkspaceLocationButton.getSelection();
    }

    protected void validate()
    {
        // TODO validate()
    }

    protected static class ArchetypeLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        /** Returns the element text */
        public String getColumnText( Object element, int columnIndex )
        {
            if( element instanceof Archetype )
            {
                Archetype archetype = (Archetype) element;

                switch( columnIndex )
                {
                    case 0:
                        return archetype.getArtifactId();
                    case 1:
                        return archetype.getVersion();
                }
            }

            return super.getText( element );
        }

        /** Returns the element text */
        public Image getColumnImage( Object element, int columnIndex )
        {
            return null;
        }
    }

    protected class VersionsFilter extends ViewerFilter implements SelectionListener
    {
        private boolean showLastVersion;
        private boolean includeSnapshots;

        public VersionsFilter( boolean showLastVersion, boolean includeSnapshots )
        {
            this.showLastVersion = showLastVersion;
            this.includeSnapshots = includeSnapshots;
        }

        public boolean select( Viewer viewer, Object parentElement, Object element )
        {
            if( !( element instanceof Archetype ) )
            {
                return false;
            }

            Archetype archetype = (Archetype) element;
            String version = archetype.getVersion();

            if( !includeSnapshots )
            {
                if( isSnapshotVersion( version ) )
                {
                    return false;
                }
            }

            if( !showLastVersion )
            {
                return true;
            }

            // need to find latest version, skipping snapshots depending on includeSnapshots
            List<String> versions = archetypeVersions.get( getArchetypeKey( archetype ) );

            if( versions == null || versions.isEmpty() )
            {
                return false; // can't really happen
            }

            for( String otherVersion : versions )
            {
                if( includeSnapshots || !isSnapshotVersion( otherVersion ) )
                {
                    if( otherVersion.equals( version ) )
                    {
                        return true;
                    }

                    break;
                }
            }

            return false;
        }

        boolean isSnapshotVersion( String version )
        {
            return !M2EUIUtils.nullOrEmpty( version ) && version.endsWith( "SNAPSHOT" ); //$NON-NLS-1$
        }

        public void widgetSelected( SelectionEvent e )
        {
            this.showLastVersion = showLastVersionButton.getSelection();
            this.includeSnapshots = includeShapshotsButton.getSelection();
            viewer.refresh();
            Archetype archetype = getArchetype();
            // can be null in some cases, don't try to reveal
            if( archetype != null )
            {
                viewer.reveal( archetype );
            }
            viewer.getTable().setSelection( viewer.getTable().getSelectionIndex() );
            viewer.getTable().setFocus();
        }

        public void widgetDefaultSelected( SelectionEvent e )
        {
        }
    }

    private static String getArchetypeKey( Archetype archetype )
    {
        return archetype.getGroupId() + ":" + archetype.getArtifactId(); //$NON-NLS-1$
    }

    private static class Msgs extends NLS
    {
        public static String includeSnapshot;
        public static String showLastVersion;
        public static String version;
        public static String archetype;
        public static String browse;
        public static String selectLocation;
        public static String location;
        public static String useDefaultWorkspaceLocation;
        public static String newLiferayProject;
        public static String newLiferayProjectDesc;
        public static String projectName;

        static
        {
            initializeMessages( LiferayMavenProjectWizardPage.class.getName(), Msgs.class );
        }
    }

}
