
package com.liferay.ide.portal.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.portal.core.structures.model.DynamicElement;
import com.liferay.ide.portal.core.structures.model.DynamicElementMetadata;
import com.liferay.ide.portal.core.structures.model.Entry;
import com.liferay.ide.portal.core.structures.model.Root;
import com.liferay.ide.portal.core.structures.model.Structure;
import com.liferay.ide.portal.core.structures.model.StructureRoot;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class PortalCoreTests extends BaseTests
{
    private static final IPath DDM_STRUCTURE_BASIC_DOCUMENT = new Path( "structures/ddm_structure_basic_document.xml" );
    private static final IPath DDM_STRUCTURE_DDL = new Path( "structures/ddm_structure_ddl.xml" );
    private static final IPath DDMSTRUCTURE = new Path( "structures/ddmstructure.xml" );
    private static final IPath DOCUMENT_LIBRARY_STRUCTURES = new Path( "structures/document-library-structures.xml" );
    private static final IPath DYNAMIC_DATA_MAPPING_STRUCTURES = new Path( "structures/dynamic-data-mapping-structures.xml" );
    private static final IPath TEST_DDM_STRUCTURE_ALL_FIELDS = new Path( "structures/test-ddm-structure-all-fields.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_BOOLEAN_REPEATABLE_FIELD = new Path( "structures/test-journal-content-boolean-repeatable-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_DOC_LIBRARY_FIELD = new Path( "structures/test-journal-content-doc-library-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_LINK_TO_PAGE_FIELD = new Path( "structures/test-journal-content-link-to-page-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_LIST_FIELD = new Path( "structures/test-journal-content-list-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_MULTI_LIST_FIELD = new Path( "structures/test-journal-content-multi-list-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_NESTED_FIELDS = new Path( "structures/test-journal-content-nested-fields.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_TEXT_AREA_FIELD = new Path( "structures/test-journal-content-text-area-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_TEXT_BOX_REPEATABLE_FIELD = new Path( "structures/test-journal-content-text-box-repeatable-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_TEXT_FIELD = new Path( "structures/test-journal-content-text-field.xml" );

    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }

    @Test
    public void testDDMStructureBasicDocumentRead() throws Exception
    {
        final Element element = getElementFromFile( this.a, DDM_STRUCTURE_BASIC_DOCUMENT, StructureRoot.TYPE );

        assertNotNull( element );

        final StructureRoot root = element.nearest( StructureRoot.class );

        assertNotNull( root );

        assertEquals( root.getAvailableLocales().content(), "en_US" );

        assertEquals( root.getDefaultLocale().content(), "en_US" );

        final DynamicElement dynamicElement = root.getDynamicElements().get( 1 );

        assertNotNull( dynamicElement );

        assertEquals( dynamicElement.getDataType().content( false ), "string" );

        assertEquals( dynamicElement.getName().content( false ), "ClimateForcast_COMMAND_LINE" );

        assertEquals( dynamicElement.getType().content( false ), "text" );

        final DynamicElementMetadata metaData = dynamicElement.getMetadata().content( false );

        assertNotNull( metaData );

        assertEquals( metaData.getLocale().content( false ), "en_US" );

        final ElementList<Entry> entries = metaData.getEntries();

        assertNotNull( entries );

        assertEquals( entries.size(), 4 );

        final Entry entry = entries.get( 2 );

        assertNotNull( entry );

        assertEquals( entry.getName().content( false ), "required" );

        assertEquals( entry.getValue().content( false ), "false" );
    }

    @Test
    public void testDocumentLibraryStructuresRead() throws Exception
    {
        final Element element = getElementFromFile( this.a, DOCUMENT_LIBRARY_STRUCTURES, Root.TYPE );

        assertNotNull( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );

        final ElementList<Structure> structures = root.getStructures();

        assertNotNull( structures );

        assertEquals( structures.size(), 8 );

        final Structure structure = structures.get( 2 );

        assertNotNull( structure );

        assertEquals( structure.getName().content( false ), "Learning Module Metadata" );

        assertEquals( structure.getDescription().content( false ), "Learning Module Metadata" );

        final StructureRoot structureRoot = structure.getRoot();

        assertNotNull( structureRoot );

        assertEquals( structureRoot.getAvailableLocales().content( false ), "[$LOCALE_DEFAULT$]" );

        assertEquals( structureRoot.getDefaultLocale().content( false ), "[$LOCALE_DEFAULT$]" );

        final ElementList<DynamicElement> dynamicElements = structureRoot.getDynamicElements();

        assertNotNull( dynamicElements );

        assertEquals( dynamicElements.size(), 4 );
    }

}
