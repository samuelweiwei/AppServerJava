package com.loraiot.iot.comm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;



/**
 * The factory to compose suitable connection for the user to connect CSIF.
 * @author 10028484
 * @version 0.0.1
 */
public class ConnectionFactory {	
	
	/**
	 * Connection object to offer to user, staticlly means the process only owns one copy of the variable.
	 */
	public static Connection conn = null;
	
	/**
	 * Default constructor
	 */
	public ConnectionFactory() {
		super();	
	}	
	
	
	/**
	 * Method to compose the connection object according to the connection type.
	 * @param host The CSIF host address.
	 * @param port The CSIF connection port.
	 * @param type The connection type, TCP, SSL or UDP.
	 * @return
	 * @throws IOException Connect Exception.
	 */
	public static synchronized Connection getConnect(InetAddress host, int port, String type) throws IOException{	
		type=type.trim().toUpperCase();
		switch (type){
			case "TCP": conn = new TCPConnection();break;
			case "UDP": conn = new UDPConnection();break;
			case "SSL": conn = new SSLConnection();break;
			default:
				conn = new SSLConnection();
		}		
		conn.getConnection(host, port);
		return conn;			
	}
	

}
