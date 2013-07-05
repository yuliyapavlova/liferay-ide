/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.ui.editor.internal;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.services.RelativePathService;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphirePropertyEditorCondition;

/**
 * @author Kamesh Sampath
 */
public class ResourceBundleJumpActionHandlerCondition extends SapphirePropertyEditorCondition
{

    @Override
    protected boolean evaluate( PropertyEditorPart part )
    {
        final Element element = part.getModelElement();
        final Property property = part.property();

        return( property.definition() instanceof ValueProperty &&
            Path.class.isAssignableFrom( property.definition().getTypeClass() ) && element.service( RelativePathService.class ) != null );
    }

}
