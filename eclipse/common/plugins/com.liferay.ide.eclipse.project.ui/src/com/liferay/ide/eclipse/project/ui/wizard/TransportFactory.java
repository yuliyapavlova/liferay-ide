/*******************************************************************************
 * Copyright (c) 2010 The Eclipse Foundation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Eclipse Foundation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.eclipse.project.ui.wizard;

import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import javax.naming.ServiceUnavailableException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Factory to retrieve Transport instances of p2. Will delegate to version-dependent implementations.
 * 
 * @author David Green
 * @author Benjamin Muskalla
 */
public abstract class TransportFactory {

	private static final String[] factoryClasses = new String[] { //

	"org.eclipse.epp.internal.mpc.core.util.JavaPlatformTransportFactory" }; //$NON-NLS-1$

	private static TransportFactory instance;

	public static synchronized TransportFactory instance() {
		if (instance == null) {
			for (String factoryClass : factoryClasses) {
				TransportFactory factory;
				try {
					factory = (TransportFactory) Class.forName(factoryClass,true,TransportFactory.class.getClassLoader()).newInstance();
				} catch (Throwable t) {
					// ignore
					continue;
				}
				if (factory.isAvailable()) {
					instance = factory;
					break;
				}
			}
			if (instance == null) {
				throw new IllegalStateException();
			}
		}
		return instance;
	}


	public ITransport getTransport() {
		return new ITransport() {

			public InputStream stream(URI location, IProgressMonitor monitor) throws FileNotFoundException,
			CoreException {
				try {
					return invokeStream(location, monitor);
				} catch (Exception e) {
				}
				return null;
			}

		};
	}

	protected abstract boolean isAvailable();

	protected abstract InputStream invokeStream(URI location, IProgressMonitor monitor) throws Exception;

	protected void handleStreamExceptions(Exception e) throws ServiceUnavailableException, CoreException,
	FileNotFoundException {
		if (e instanceof InvocationTargetException) {
			InvocationTargetException targetException = (InvocationTargetException) e;
			Throwable cause = targetException.getCause();
			if (cause instanceof CoreException) {
				CoreException coreCause = (CoreException) cause;
				handleServiceUnavailable(coreCause);
				throw coreCause;
			} else if (cause instanceof FileNotFoundException) {
				throw (FileNotFoundException) cause;
			}

		} else {
			throw new CoreException( new Status( IStatus.ERROR, ProjectUIPlugin.PLUGIN_ID, e.getMessage(), e ) );
		}
	}


	protected static void handleServiceUnavailable( CoreException e ) throws CoreException {
		if (e.getStatus().getCode() == 1002) {
			Throwable cause = e.getCause();
			if (cause != null && cause.getMessage() != null && cause.getMessage().indexOf("503") != -1) { //$NON-NLS-1$
				throw new CoreException( new Status( IStatus.ERROR, ProjectUIPlugin.PLUGIN_ID, 503, "503", e ) );
			}
		}
	}

}