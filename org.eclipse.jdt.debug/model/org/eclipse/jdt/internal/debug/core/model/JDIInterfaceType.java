/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.core.model;

 
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaInterfaceType;

import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;

/**
 * The interface of an object in a debug target.
 */
public class JDIInterfaceType extends JDIReferenceType implements IJavaInterfaceType {
	
	/**
	 * Cosntructs a new interface type on the given target referencing
	 * the specified interface type.
	 */
	public JDIInterfaceType(JDIDebugTarget target, InterfaceType type) {
		super(target, type);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.debug.core.IJavaInterfaceType#getImplementors()
	 */
	public IJavaClassType[] getImplementors() throws DebugException {
		try {
			List implementorList = ((InterfaceType)getUnderlyingType()).implementors();
			List javaClassTypeList = new ArrayList(implementorList.size());
			Iterator iterator = implementorList.iterator();
			while (iterator.hasNext()) {
				ClassType classType = (ClassType) iterator.next();
				if (classType != null) {
					javaClassTypeList.add(JDIType.createType(getDebugTarget(), classType));
				}				
			}
			IJavaClassType[] javaClassTypeArray = new IJavaClassType[javaClassTypeList.size()];
			javaClassTypeArray = (IJavaClassType[]) javaClassTypeList.toArray(javaClassTypeArray);
			return javaClassTypeArray;
		} catch (RuntimeException re) {
			getDebugTarget().targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.getString("JDIClassType.exception_while_retrieving_superclass"), new String[] {re.toString()}), re); //$NON-NLS-1$
		}
		return new IJavaClassType[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.debug.core.IJavaInterfaceType#getSubInterfaces()
	 */
	public IJavaInterfaceType[] getSubInterfaces() throws DebugException {
		try {
			List subList = ((InterfaceType)getUnderlyingType()).subinterfaces();
			List javaInterfaceTypeList = new ArrayList(subList.size());
			Iterator iterator = subList.iterator();
			while (iterator.hasNext()) {
				InterfaceType interfaceType = (InterfaceType) iterator.next();
				if (interfaceType != null) {
					javaInterfaceTypeList.add(JDIType.createType(getDebugTarget(), interfaceType));
				}				
			}
			IJavaInterfaceType[] javaInterfaceTypeArray = new IJavaInterfaceType[javaInterfaceTypeList.size()];
			javaInterfaceTypeArray = (IJavaInterfaceType[]) javaInterfaceTypeList.toArray(javaInterfaceTypeArray);
			return javaInterfaceTypeArray;
		} catch (RuntimeException re) {
			getDebugTarget().targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.getString("JDIClassType.exception_while_retrieving_superclass"), new String[] {re.toString()}), re); //$NON-NLS-1$
		}
		return new IJavaInterfaceType[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.debug.core.IJavaInterfaceType#getSuperInterfaces()
	 */
	public IJavaInterfaceType[] getSuperInterfaces() throws DebugException {
		try {
			List superList = ((InterfaceType)getUnderlyingType()).superinterfaces();
			List javaInterfaceTypeList = new ArrayList(superList.size());
			Iterator iterator = superList.iterator();
			while (iterator.hasNext()) {
				InterfaceType interfaceType = (InterfaceType) iterator.next();
				if (interfaceType != null) {
					javaInterfaceTypeList.add(JDIType.createType(getDebugTarget(), interfaceType));
				}				
			}
			IJavaInterfaceType[] javaInterfaceTypeArray = new IJavaInterfaceType[javaInterfaceTypeList.size()];
			javaInterfaceTypeArray = (IJavaInterfaceType[]) javaInterfaceTypeList.toArray(javaInterfaceTypeArray);
			return javaInterfaceTypeArray;
		} catch (RuntimeException re) {
			getDebugTarget().targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.getString("JDIClassType.exception_while_retrieving_superclass"), new String[] {re.toString()}), re); //$NON-NLS-1$
		}
		return new IJavaInterfaceType[0];
	}

}

