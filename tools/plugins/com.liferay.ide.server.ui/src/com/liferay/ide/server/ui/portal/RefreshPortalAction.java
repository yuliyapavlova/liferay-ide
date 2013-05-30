/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.server.ui.portal;


import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * "Open" menu action.
 */
public class RefreshPortalAction extends AbstractPortalAction
{

	/**
	 * OpenAction constructor.
	 *
	 * @param sp
	 *            a selection provider
	 */
	public RefreshPortalAction( ISelectionProvider sp )
	{
        super( sp, "Refresh" ); //$NON-NLS-1$

        // setActionDefinitionId( org.eclipse.ui.actions.RefreshAction.ID );
	}

	@Override
	public void perform( final Object node )
	{
		if( this.getSelectionProvider() instanceof CommonViewer )
		{
			if( node instanceof RemoteFolder )
			{
				( (RemoteFolder) node ).clearCache();
			}

			final CommonViewer viewer = (CommonViewer) getSelectionProvider();

			Display.getDefault().asyncExec( new Runnable()
			{
				public void run()
				{
					viewer.refresh( true );
				}
			} );

		}
	}

}
