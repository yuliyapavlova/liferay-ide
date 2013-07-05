package com.liferay.ide.portal.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * @author Gregory Amerson
 */
public class EntryValueBinding extends XmlValueBindingImpl
{

    private XmlPath path;

    @Override
    public void init( Property property )
    {
        super.init( property );

        final XmlBinding bindingAnnotation = property.definition().getAnnotation( XmlBinding.class );

        this.path = new XmlPath( bindingAnnotation.path(), resource().getXmlNamespaceResolver() );
    }

    @Override
    public String read()
    {
        String retval = null;

        XmlElement xmlElement = xml( false );

        if( xmlElement != null )
        {
            retval = xmlElement.getChildNodeText( this.path );
        }

        return retval;
    }

    @Override
    public void write( String value )
    {
        XmlElement xmlElement = xml( true );

        Node childNode = xmlElement.getChildNode( this.path, true ).getDomNode();

        CoreUtil.removeChildren( childNode );

        Document document = childNode.getOwnerDocument();

        childNode.insertBefore( document.createCDATASection( value ), null );
    }

}
