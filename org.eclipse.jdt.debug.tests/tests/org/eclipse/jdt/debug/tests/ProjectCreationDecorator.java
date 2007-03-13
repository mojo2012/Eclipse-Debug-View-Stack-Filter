/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.debug.tests;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.debug.testplugin.JavaProjectHelper;
import org.eclipse.jdt.debug.testplugin.JavaTestPlugin;
import org.eclipse.jdt.debug.ui.IJavaDebugUIConstants;
import org.eclipse.jdt.internal.debug.ui.IJDIPreferencesConstants;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.util.PrefUtil;

/**
 * Test to close the workbench, since debug tests do not run in the UI thread.
 */
public class ProjectCreationDecorator extends AbstractDebugTest {

    /**
     * Constructor
     * @param name
     */
    public ProjectCreationDecorator(String name) {
        super(name);
    }

    /**
     * 
     */
    public void testPerspectiveSwtich() {
        DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() {
            public void run() {
                IWorkbench workbench = PlatformUI.getWorkbench();
                IPerspectiveDescriptor descriptor = workbench.getPerspectiveRegistry().findPerspectiveWithId(IDebugUIConstants.ID_DEBUG_PERSPECTIVE);
                IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
				activePage.setPerspective(descriptor);
				// hide variables and breakpoints view to reduce simultaneous conflicting requests on debug targets
                IViewReference ref = activePage.findViewReference(IDebugUIConstants.ID_VARIABLE_VIEW);
                activePage.hideView(ref);
                ref = activePage.findViewReference(IDebugUIConstants.ID_BREAKPOINT_VIEW);
                activePage.hideView(ref);
            }
        });
    }

    /**
     * @throws Exception
     */
    public void testProjectCreation() throws Exception {
        // delete any pre-existing project
        IProject pro = ResourcesPlugin.getWorkspace().getRoot().getProject("DebugTests");
        if (pro.exists()) {
            pro.delete(true, true, null);
        }
        // create project and import source
        fJavaProject = JavaProjectHelper.createJavaProject("DebugTests", "bin");
        IPackageFragmentRoot src = JavaProjectHelper.addSourceContainer(fJavaProject, "src");
        File root = JavaTestPlugin.getDefault().getFileInPlugin(JavaProjectHelper.TEST_SRC_DIR);
        JavaProjectHelper.importFilesFromDirectory(root, src.getPath(), null);

        // add rt.jar
        IVMInstall vm = JavaRuntime.getDefaultVMInstall();
        assertNotNull("No default JRE", vm);
        JavaProjectHelper.addContainerEntry(fJavaProject, new Path(JavaRuntime.JRE_CONTAINER));
        pro = fJavaProject.getProject();

        // add A.jar
        root = JavaTestPlugin.getDefault().getFileInPlugin(new Path("testjars"));
        JavaProjectHelper.importFilesFromDirectory(root, src.getPath(), null);
        IPath path = src.getPath().append("A.jar");
        JavaProjectHelper.addLibrary(fJavaProject, path);

        // create launch configuration folder

        IFolder folder = pro.getFolder("launchConfigurations");
        if (folder.exists()) {
            folder.delete(true, null);
        }
        folder.create(true, true, null);

        // delete any existing launch configs
        ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations();
        for (int i = 0; i < configs.length; i++) {
            configs[i].delete();
        }

        // create launch configurations
        createLaunchConfiguration("LargeSourceFile");
        createLaunchConfiguration("LotsOfFields");
        createLaunchConfiguration("Breakpoints");
        createLaunchConfiguration("InstanceVariablesTests");
        createLaunchConfiguration("LocalVariablesTests");
        createLaunchConfiguration("StaticVariablesTests");
        createLaunchConfiguration("DropTests");
        createLaunchConfiguration("ThrowsNPE");
        createLaunchConfiguration("ThrowsException");
        createLaunchConfiguration("org.eclipse.debug.tests.targets.Watchpoint");
        createLaunchConfiguration("A");
        createLaunchConfiguration("HitCountLooper");
        createLaunchConfiguration("CompileError");
        createLaunchConfiguration("MultiThreadedLoop");
        createLaunchConfiguration("HitCountException");
        createLaunchConfiguration("MultiThreadedException");
        createLaunchConfiguration("MultiThreadedList");
        createLaunchConfiguration("MethodLoop");
        createLaunchConfiguration("StepFilterOne");

        createLaunchConfiguration("EvalArrayTests");
        createLaunchConfiguration("EvalSimpleTests");
        createLaunchConfiguration("EvalTypeTests");
        createLaunchConfiguration("EvalNestedTypeTests");
        createLaunchConfiguration("EvalTypeHierarchyTests");
        createLaunchConfiguration("WorkingDirectoryTest");
        createLaunchConfiguration("OneToTen");
        createLaunchConfiguration("OneToTenPrint");
        createLaunchConfiguration("FloodConsole");
        createLaunchConfiguration("ConditionalStepReturn");
        createLaunchConfiguration("VariableChanges");
        createLaunchConfiguration("DefPkgReturnType");
        createLaunchConfiguration("InstanceFilterObject");
        createLaunchConfiguration("org.eclipse.debug.tests.targets.CallStack");
        createLaunchConfiguration("org.eclipse.debug.tests.targets.HcrClass");
        createLaunchConfiguration("org.eclipse.debug.tests.targets.StepIntoSelectionClass");
        createLaunchConfiguration("WatchItemTests");
        createLaunchConfiguration("ArrayTests");
        createLaunchConfiguration("PerfLoop");
        createLaunchConfiguration("Console80Chars");
        createLaunchConfiguration("ConsoleStackTrace");
        createLaunchConfiguration("ConsoleVariableLineLength");
        createLaunchConfiguration("StackTraces");
        createLaunchConfiguration("ConsoleInput");
        createLaunchConfiguration("PrintConcatenation");
        createLaunchConfiguration("VariableDetails");
        createLaunchConfiguration("org.eclipse.debug.tests.targets.ArrayDetailTests");
        createLaunchConfiguration("ArrayDetailTestsDef");
        createLaunchConfiguration("ForceReturnTests");
        
        //launch history tests
        createLaunchConfiguration("LaunchHistoryTest");
        createLaunchConfiguration("LaunchHistoryTest2");
    }

    /**
     * Create a project with non-default, multiple output locations.
     * 
     * @throws Exception
     */
    public void testMultipleOutputProjectCreation() throws Exception {
        // delete any pre-existing project
        IProject pro = ResourcesPlugin.getWorkspace().getRoot().getProject("MultiOutput");
        if (pro.exists()) {
            pro.delete(true, true, null);
        }
        // create project with two src folders and output locations
        IJavaProject project = JavaProjectHelper.createJavaProject("MultiOutput");
        JavaProjectHelper.addSourceContainer(project, "src1", "bin1");
        JavaProjectHelper.addSourceContainer(project, "src2", "bin2");

        // add rt.jar
        IVMInstall vm = JavaRuntime.getDefaultVMInstall();
        assertNotNull("No default JRE", vm);
        JavaProjectHelper.addContainerEntry(project, new Path(JavaRuntime.JRE_CONTAINER));
    }
    
    /**
     * Create a project bound to a specific JRE
     * 
     * @throws Exception
     */
    public void testProjectBoundToJRECreation() throws Exception {
        // delete any pre-existing project
        IProject pro = ResourcesPlugin.getWorkspace().getRoot().getProject("BoundJRE");
        if (pro.exists()) {
            pro.delete(true, true, null);
        }
        // create project with two src folders and output locations
        IJavaProject project = JavaProjectHelper.createJavaProject("BoundJRE");
        JavaProjectHelper.addSourceContainer(project, "src", "bin");

        // add VM specific JRE container
        IPath path = JavaRuntime.newJREContainerPath(JavaRuntime.getDefaultVMInstall());
        JavaProjectHelper.addContainerEntry(project, path);
    }   
    
    /**
     * Create a project bound to a specific Execution Environment
     * 
     * @throws Exception
     */
    public void testProjectBoundToEECreation() throws Exception {
        // delete any pre-existing project
        IProject pro = ResourcesPlugin.getWorkspace().getRoot().getProject("BoundEE");
        if (pro.exists()) {
            pro.delete(true, true, null);
        }
        // create project with two src folders and output locations
        IJavaProject project = JavaProjectHelper.createJavaProject("BoundEE");
        JavaProjectHelper.addSourceContainer(project, "src", "bin");

        // add VM specific JRE container
        IExecutionEnvironment j2se14 = JavaRuntime.getExecutionEnvironmentsManager().getEnvironment("J2SE-1.4");
        assertNotNull("Missing J2SE-1.4 environment", j2se14);
        IPath path = JavaRuntime.newJREContainerPath(j2se14);
        JavaProjectHelper.addContainerEntry(project, path);
    }     

    /**
     * Set up preferences that need to be changed for the tests
     */
    public void testSetPreferences() {
        IPreferenceStore debugUIPreferences = DebugUIPlugin.getDefault().getPreferenceStore();
        // Don't prompt for perspective switching
        debugUIPreferences.setValue(IInternalDebugUIConstants.PREF_SWITCH_PERSPECTIVE_ON_SUSPEND, MessageDialogWithToggle.ALWAYS);
        debugUIPreferences.setValue(IInternalDebugUIConstants.PREF_SWITCH_TO_PERSPECTIVE, MessageDialogWithToggle.ALWAYS);
        debugUIPreferences.setValue(IInternalDebugUIConstants.PREF_RELAUNCH_IN_DEBUG_MODE, MessageDialogWithToggle.NEVER);
        debugUIPreferences.setValue(IInternalDebugUIConstants.PREF_WAIT_FOR_BUILD, MessageDialogWithToggle.ALWAYS);
        debugUIPreferences.setValue(IInternalDebugUIConstants.PREF_CONTINUE_WITH_COMPILE_ERROR, MessageDialogWithToggle.ALWAYS);
        debugUIPreferences.setValue(IInternalDebugUIConstants.PREF_SAVE_DIRTY_EDITORS_BEFORE_LAUNCH, MessageDialogWithToggle.NEVER);

        String property = System.getProperty("debug.workbenchActivation");
        if (property != null && property.equals("off")) {
            debugUIPreferences.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
            debugUIPreferences.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
            debugUIPreferences.setValue(IInternalDebugUIConstants.PREF_ACTIVATE_DEBUG_VIEW, false);
            debugUIPreferences.setValue(IDebugUIConstants.PREF_ACTIVATE_WORKBENCH, false);
        }

        IPreferenceStore jdiUIPreferences = JDIDebugUIPlugin.getDefault().getPreferenceStore();
        // Turn off suspend on uncaught exceptions
        jdiUIPreferences.setValue(IJDIPreferencesConstants.PREF_SUSPEND_ON_UNCAUGHT_EXCEPTIONS, false);
        jdiUIPreferences.setValue(IJDIPreferencesConstants.PREF_SUSPEND_ON_COMPILATION_ERRORS, false);
        // Don't warn about HCR failures
        jdiUIPreferences.setValue(IJDIPreferencesConstants.PREF_ALERT_HCR_FAILED, false);
        jdiUIPreferences.setValue(IJDIPreferencesConstants.PREF_ALERT_HCR_NOT_SUPPORTED, false);
        jdiUIPreferences.setValue(IJDIPreferencesConstants.PREF_ALERT_OBSOLETE_METHODS, false);
        // Set the timeout preference to a high value, to avoid timeouts while
        // testing
        JDIDebugModel.getPreferences().setDefault(JDIDebugModel.PREF_REQUEST_TIMEOUT, 10000);
        // turn off monitor information
        jdiUIPreferences.setValue(IJavaDebugUIConstants.PREF_SHOW_MONITOR_THREAD_INFO, false);
        
        // turn off workbench heap monitor
        PrefUtil.getAPIPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR, false);
        IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
        for (int i = 0; i < windows.length; i++) {
            IWorkbenchWindow window = windows[i];
            if(window instanceof WorkbenchWindow){
                ((WorkbenchWindow) window).showHeapStatus(false);
            }
        }       
    }

    /**
     * @throws Exception
     */
    public void testBuild() throws Exception {
        // force a full build and wait
        ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
        waitForBuild();
    }

    /**
     * test if builds completed successfully and output directory contains class
     * files.
     * @throws Exception
     */
    public void testOutputFolderNotEmpty() throws Exception {
        waitForBuild();
        IPath outputLocation = fJavaProject.getOutputLocation();
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IResource resource = root.findMember(outputLocation);
        assertNotNull("Project output location is null", resource);
        assertTrue("Project output location does not exist", resource.exists());
        assertTrue("Project output is not a folder", (resource.getType() == IResource.FOLDER));
        IFolder folder = (IFolder) resource;
        IResource[] children = folder.members();
        assertTrue("output folder is empty", children.length > 0);
    }

    /**
     * @throws Exception
     */
    public void testForUnexpectedErrorsInProject() throws Exception {
        waitForBuild();
        IProject project = fJavaProject.getProject();
        IMarker[] markers = project.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
        int errors = 0;
        for (int i = 0; i < markers.length; i++) {
            IMarker marker = markers[i];
            Integer severity = (Integer) marker.getAttribute(IMarker.SEVERITY);
            if (severity != null && severity.intValue() >= IMarker.SEVERITY_ERROR) {
                errors++;
            }
        }
        assertTrue("Unexpected compile errors in project. Expected 1, found " + markers.length, errors == 1);
    }

    /**
     * @throws Exception
     */
    public void testClassFilesGenerated() throws Exception {
        waitForBuild();
        IPath outputLocation = fJavaProject.getOutputLocation();
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IFolder folder = (IFolder) root.findMember(outputLocation);
        IResource[] children = folder.members();
        int classFiles = 0;
        for (int i = 0; i < children.length; i++) {
            IResource child = children[i];
            if (child.getType() == IResource.FILE) {
                IFile file = (IFile) child;
                String fileExtension = file.getFileExtension();
                if (fileExtension.equals("class")) {
                    classFiles++;
                }
            }
        }
        assertTrue("No class files exist", (classFiles > 0));
    }
}
