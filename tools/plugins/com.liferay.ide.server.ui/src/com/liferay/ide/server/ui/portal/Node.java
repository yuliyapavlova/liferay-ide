package com.liferay.ide.server.ui.portal;


public class Node
{

    private Object parent;
    protected String displayName;

    public Node( Object parent )
    {
        this.parent = parent;
    }

    public Node( Object parent, String displayName )
    {
        this.parent = parent;
        this.displayName = displayName;
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
