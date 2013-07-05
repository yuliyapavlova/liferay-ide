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

package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
public interface LifeCycle extends Element
{

    ElementType TYPE = new ElementType( LifeCycle.class );

    // *** LifeCycle ***

    @Type( base = LifeCycleType.class )
    @Label( standard = "life cyle name" )
    @XmlBinding( path = "" )
    ValueProperty PROP_LIFE_CYCLE = new ValueProperty( TYPE, "LifeCycle" ); //$NON-NLS-1$

    Value<LifeCycleType> getLifeCycle();

    void setLifeCycle( String value );

    void setLifeCycle( LifeCycleType value );

}
