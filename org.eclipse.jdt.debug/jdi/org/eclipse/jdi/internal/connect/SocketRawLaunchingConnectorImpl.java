/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdi.internal.connect;


import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdi.internal.VirtualMachineImpl;
import org.eclipse.jdi.internal.VirtualMachineManagerImpl;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;


public class SocketRawLaunchingConnectorImpl extends ConnectorImpl implements LaunchingConnector {
	/** Time that a launched VM is given to connect to us. */
	private static final int ACCEPT_TIMEOUT = 10000;

	/** Raw command to start the debugged application VM. */
	private String fCommand;
	/** Address from which to listen for a connection after the raw command is run. */
	private String fAddress;
	
	/**
	 * Creates new SocketAttachingConnectorImpl.
	 */
	public SocketRawLaunchingConnectorImpl(VirtualMachineManagerImpl virtualMachineManager) {
		super(virtualMachineManager);
		
		// Create communication protocol specific transport.
		SocketTransportImpl transport = new SocketTransportImpl();
		setTransport(transport);
	}
	
	/**
	 * @return Returns the default arguments.
	 */	
	public Map defaultArguments() {
		HashMap arguments = new HashMap(3);
		
		// Command
		StringArgumentImpl strArg = new StringArgumentImpl("command", ConnectMessages.SocketRawLaunchingConnectorImpl_Raw_command_to_start_the_debugged_application_VM_1, ConnectMessages.SocketRawLaunchingConnectorImpl_Command_2, true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		arguments.put(strArg.name(), strArg);
		
		// Address
		strArg = new StringArgumentImpl("address", ConnectMessages.SocketRawLaunchingConnectorImpl_Address_from_which_to_listen_for_a_connection_after_the_raw_command_is_run_3, ConnectMessages.SocketRawLaunchingConnectorImpl_Address_4, true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		arguments.put(strArg.name(), strArg);
		
		// Quote
		strArg = new StringArgumentImpl("quote", ConnectMessages.SocketRawLaunchingConnectorImpl_Character_used_to_combine_space_delimited_text_into_a_single_command_line_argument_5, ConnectMessages.SocketRawLaunchingConnectorImpl_Quote_6, true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		strArg.setValue("\""); //$NON-NLS-1$
		arguments.put(strArg.name(), strArg);

		return arguments;
	}
	
	/**
	 * @return Returns a short identifier for the connector.
	 */	
	public String name() {
		return "com.sun.jdi.RawCommandLineLaunch"; //$NON-NLS-1$
	}
	
	/**
	 * @return Returns a human-readable description of this connector and its purpose.
	 */	
	public String description() {
		return ConnectMessages.SocketRawLaunchingConnectorImpl_Launches_target_using_user_specified_command_line_and_attaches_to_it_7; //$NON-NLS-1$
	}
	
 	/**
 	 * Retrieves connection arguments.
 	 */
	private void getConnectionArguments(Map connectionArgs) throws IllegalConnectorArgumentsException {
		String attribute = ""; //$NON-NLS-1$
		try {
			attribute = "command"; //$NON-NLS-1$
		 	fCommand = ((Connector.StringArgument)connectionArgs.get(attribute)).value();
		 	attribute = "address"; //$NON-NLS-1$
		 	fAddress = ((Connector.StringArgument)connectionArgs.get(attribute)).value();
		 	attribute = "quote"; //$NON-NLS-1$
		 	((Connector.StringArgument)connectionArgs.get(attribute)).value();
		} catch (ClassCastException e) {
			throw new IllegalConnectorArgumentsException(ConnectMessages.SocketRawLaunchingConnectorImpl_Connection_argument_is_not_of_the_right_type_8, attribute); //$NON-NLS-1$
		} catch (NullPointerException e) {
			throw new IllegalConnectorArgumentsException(ConnectMessages.SocketRawLaunchingConnectorImpl_Necessary_connection_argument_is_null_9, attribute); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			throw new IllegalConnectorArgumentsException(ConnectMessages.SocketRawLaunchingConnectorImpl_Connection_argument_is_not_a_number_10, attribute); //$NON-NLS-1$
		}
	}

	/**
	 * Launches an application and connects to its VM. 
	 * @return Returns a connected Virtual Machine.
	 */
	public VirtualMachine launch(Map connectionArgs) throws IOException, IllegalConnectorArgumentsException, VMStartException {
		getConnectionArguments(connectionArgs);
		
		// A listening connector is used that waits for a connection of the VM that is started up.
		// Note that port number zero means that a free port is chosen.
		SocketListeningConnectorImpl listenConnector = new SocketListeningConnectorImpl(virtualMachineManager());
		Map args = listenConnector.defaultArguments();
		((Connector.IntegerArgument)args.get("port")).setValue(fAddress); //$NON-NLS-1$
		((Connector.IntegerArgument)args.get("timeout")).setValue(ACCEPT_TIMEOUT); //$NON-NLS-1$
		listenConnector.startListening(args);
		
		// Start VM.
		Process proc = Runtime.getRuntime().exec(fCommand);

		// The accept times out it the VM does not connect.
		VirtualMachineImpl virtualMachine;
		try {
			virtualMachine = (VirtualMachineImpl)listenConnector.accept(args);
		} catch (InterruptedIOException e) {
			proc.destroy();
			String message= MessageFormat.format(ConnectMessages.SocketLaunchingConnectorImpl_VM_did_not_connect_within_given_time___0__ms_1, new String[]{((Connector.IntegerArgument)args.get("timeout")).value()}); //$NON-NLS-1$ //$NON-NLS-2$
			throw new VMStartException(message, proc);
		}
		
		virtualMachine.setLaunchedProcess(proc);
		return virtualMachine;
	}
}