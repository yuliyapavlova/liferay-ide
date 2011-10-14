/*******************************************************************************
 * Copyright (c) 2010 The Eclipse Foundation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	The Eclipse Foundation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.eclipse.project.ui.wizard;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * A runnable that downloads a resource from an URL
 * 
 * @author David Green
 */
abstract class AbstractResourceRunnable implements IRunnableWithProgress, Callable<Object> {

	protected ResourceProvider resourceProvider;

	protected String resourceUrl;

	private final IProgressMonitor cancellationMonitor;

	public AbstractResourceRunnable(IProgressMonitor cancellationMonitor, ResourceProvider resourceProvider,
			String resourceUrl) {
		this.cancellationMonitor = cancellationMonitor;
		this.resourceProvider = resourceProvider;
		this.resourceUrl = resourceUrl;
	}

	public Object call() throws Exception {
		run(new NullProgressMonitor() {
			@Override
			public boolean isCanceled() {
				return cancellationMonitor.isCanceled();
			}
		});
		return this;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			URL imageUrl = new URL(resourceUrl);

			InputStream in = TransportFactory.instance().getTransport().stream(imageUrl.toURI(), monitor);
			try {
				resourceProvider.putResource(resourceUrl, in);
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
		catch ( Exception e ) {
		}
		if (resourceProvider.containsResource(resourceUrl)) {
			resourceRetrieved();
		}
	}

	protected abstract void resourceRetrieved();

}
