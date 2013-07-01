package com.liferay.ide.hook.ui;

import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.util.HookUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;


public class CustomJSPFileTester extends PropertyTester
{

    public CustomJSPFileTester()
    {
        super();
    }

    public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
    {
        if( receiver instanceof IFile )
        {
            final IFile file = (IFile) receiver;
            final IProject project = file.getProject();

            if( ProjectUtil.isHookProject( project ) )
            {
                Hook hookModel = HookUtil.getHookModel( project );

                IFolder customJspFolder = HookUtil.getCustomJspFolder( hookModel, project );

                IContainer fileParent = file.getParent();

                while( fileParent != null && ! fileParent.equals( customJspFolder ) )
                {
                    fileParent = fileParent.getParent();
                }

                if( fileParent.equals( customJspFolder ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

}
