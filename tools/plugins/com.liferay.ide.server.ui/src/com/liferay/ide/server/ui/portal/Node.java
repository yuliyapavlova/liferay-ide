package com.liferay.ide.server.ui.portal;


public class Node
{

    private Object parent;
    private String displayName;
    private String id;

    public Node( Object parent )
    {
        this.parent = parent;
    }

    public Node( Object parent, String displayName )
    {
        this.parent = parent;
        this.displayName = displayName;
    }
    
    public String getId()
    {
        return this.id;
    }
    
    public void setId( String id )
    {
        this.id = id;
    }

    public Object getParent()
    {
        return this.parent;
    }

    public void setParent( Object parent )
    {
        this.parent = parent;
    }
    
    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }
}
