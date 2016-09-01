/**
 * 
 */
package com.loraiot.iot.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import com.loraiot.iot.service.Configure;

/**
 * The TCP connection to CSIF server.
 * @author 10028484
 * @version 0.0.1
 */
public class TCPConnection implements Connection {
	
	/**
	 * The internal socket to connect to CSIF.
	 */
	private Socket socket = null;
	
	/**
	 * The host CSIF server Socket address object.
	 */
	private SocketAddress address = null;
	

	/**
	 * Default constructor.
	 */
	public TCPConnection() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.loraiot.iot.comm.Connection#connect(java.net.InetSocketAddress) 
	 */
	@Override
	public Socket getConnection(InetAddress host, int port) throws IOException {
		// TODO Auto-generated method stub

		this.socket = new Socket();
		if ((host == null) || (port <= 0) || (port > 65535)) {
			this.address = new InetSocketAddress(Configure.hostip, Configure.port);
			this.socket.connect(address);
		} else {
			address = new InetSocketAddress(host, port);
			this.socket.connect(address);
		}

		return this.socket;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.loraiot.iot.comm.Connection#disconnect()
	 */
	@Override
	public synchronized boolean disconnect() throws IOException {
		// TODO Auto-generated method stub
		if (this.socket != null) {
			socket.close();
			this.socket = null;
			return true;
		}
		return false;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.loraiot.iot.comm.Connection#putData(byte[])
	 */
	@Override
	public boolean putData(byte[] data) throws IOException {
		if ((this.socket != null) && (data != null)) {
			OutputStream os = this.socket.getOutputStream();
			os.write(data);
			os.flush();
			return true;
		} else {
			return false;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.loraiot.iot.comm.Connection#getData()
	 */
	@Override
	public byte[] getData() throws IOException {
		if (this.socket != null) {
			InputStream is = this.socket.getInputStream();
			byte[] buf = new byte[2048];
			int len = is.read(buf);
			if (len <= 0){
				System.out.println("read data length is "+len);
				return null;
			}
			return buf;
		} else {
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.loraiot.iot.comm.Connection#isClosed()
	 */
	@Override
	public synchronized boolean isClosed() {
		if (this.socket != null) {
			return this.socket.isClosed();
		} else {
			return true;
		}
	}
	
	
}
