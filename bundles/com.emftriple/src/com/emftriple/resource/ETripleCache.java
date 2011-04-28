/*******************************************************************************
 * Copyright (c) 2011 Guillaume Hillairet.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Guillaume Hillairet - initial API and implementation
 *******************************************************************************/
package com.emftriple.resource;

import org.eclipse.emf.ecore.EObject;

public interface ETripleCache {

	boolean hasKey(String key);
	
	boolean hasObject(EObject obj);
	
	EObject getObjectByKey(String key);
	
	String getObjectId(EObject obj);
	
	void cache(String key, EObject obj);
	
}
