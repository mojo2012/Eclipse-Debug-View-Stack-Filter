/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.ui.breakpoints;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaWatchpoint;
import org.eclipse.jdt.internal.debug.ui.propertypages.PropertyPageMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @since 3.6
 */
public class WatchpointEditor extends StandardJavaBreakpointEditor {
	
	/**
     * Property id for access/modification suspend.
     */
    public static final int PROP_ACCESS = 0x1010;
    public static final int PROP_MODIFICATION = 0x1011;
	
	// Watchpoint editors
	private Button fAccess;
	private Button fModification;

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.ui.breakpoints.StandardJavaBreakpointEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public Control createControl(Composite parent) {
		Composite composite = SWTFactory.createComposite(parent, parent.getFont(), 2, 1, GridData.FILL_BOTH, 0, 0);
		// add standard controls
		super.createStandardControls(composite);
		Composite watchComp = SWTFactory.createComposite(composite, parent.getFont(), 3, 1, GridData.FILL_BOTH, 5, 5);
		Label label = SWTFactory.createLabel(watchComp, PropertyPageMessages.JavaLineBreakpointPage_6, 1);
		GridData gd = (GridData) label.getLayoutData();
		gd.horizontalAlignment = SWT.LEFT;
		gd.grabExcessHorizontalSpace = false;
		fAccess = createSusupendPropertyEditor(watchComp, PropertyPageMessages.JavaLineBreakpointPage_7, PROP_ACCESS);
		fModification = createSusupendPropertyEditor(watchComp, PropertyPageMessages.JavaLineBreakpointPage_8, PROP_MODIFICATION);
		return composite;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.ui.breakpoints.StandardJavaBreakpointEditor#setBreakpoint(org.eclipse.jdt.debug.core.IJavaBreakpoint)
	 */
	protected void setBreakpoint(IJavaBreakpoint breakpoint) throws CoreException {
		super.setBreakpoint(breakpoint);
		if (breakpoint instanceof IJavaWatchpoint) {
			IJavaWatchpoint watchpoint = (IJavaWatchpoint) breakpoint;
			fAccess.setEnabled(true);
			fModification.setEnabled(true);
			fAccess.setSelection(watchpoint.isAccess());
			fModification.setSelection(watchpoint.isModification());
		} else {
			fAccess.setEnabled(false);
			fModification.setEnabled(false);
			fAccess.setSelection(false);
			fModification.setSelection(false);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.debug.ui.breakpoints.StandardJavaBreakpointEditor#doSave()
	 */
	public void doSave() throws CoreException {
		super.doSave();
		IJavaBreakpoint breakpoint = getBreakpoint();
		if (breakpoint instanceof IJavaWatchpoint) {
			IJavaWatchpoint watchpoint = (IJavaWatchpoint) breakpoint;
			watchpoint.setAccess(fAccess.getSelection());
			watchpoint.setModification(fModification.getSelection());
		}
	}
}