package com.liferay.ide.hook.ui.action;

import com.liferay.ide.ui.action.AbstractObjectAction;

import org.eclipse.jface.action.IAction;


public class CustomJSP extends AbstractObjectAction
{

    public void run( IAction action )
    {
        System.out.println(action);
    }

}
