package com.liferay.ide.server.ui.portal;

import org.eclipse.osgi.util.NLS;
import org.json.JSONException;
import org.json.JSONObject;


public class TemplateEntry extends Node

{

    private final String ID_PATTERN = "{0}#{1}#{2}"; //$NON-NLS-1$
    private String script = null;

    public TemplateEntry( Object parent )
    {
        super( parent );
    }

    public void initFromJSON( JSONObject template )
    {
        try
        {
            setDisplayName( template.getString( "nameCurrentValue" ) ); //$NON-NLS-1$

            // template ids are made up of
            // companyId#companyGroupId#templateKey
            long companyId = template.getLong( "companyId" ); //$NON-NLS-1$
//            long groupId = template.getLong( "groupId" ); //$NON-NLS-1$
            long companyGroupId = template.getLong( "companyGroupId" ); //$NON-NLS-1$
            long templateKey = template.getLong( "templateKey" ); //$NON-NLS-1$

            setId( NLS.bind( ID_PATTERN, new Object[] { companyId, companyGroupId, templateKey } ) );

            this.script = template.getString( "script" ); //$NON-NLS-1$
        }
        catch( JSONException e )
        {
            e.printStackTrace();
        }
    }

    public String getContent()
    {
        return this.script;
    }

}
