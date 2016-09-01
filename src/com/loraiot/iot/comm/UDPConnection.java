/**
 * 
 */
package com.loraiot.iot.comm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;


import com.loraiot.iot.service.Configure;

/**
 * The UDP connection to CSIF server.
 * @author 10028484
 * @version 0.0.1
 */
public class UDPConnection implements Connection {
	
	/**
	 * The internal datagram socket to connect to CSIF.
	 */
	private DatagramSocket dgsocket = null;
	
	/**
	 * The host CSIF server Socket address object.
	 */
	private SocketAddress address = null;


	
	/**
	 * Default constructor.
	 */
	public UDPConnection() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.loraiot.iot.comm.Connection#getConnection()
	 */
	@Override
	public DatagramSocket getConnection(InetAddress host, int port) throws IOException {
		// TODO Auto-generated method stub
		this.dgsocket = new DatagramSocket();
		if ((host == null) || (port <= 0 || port > 25535)) {
			this.address = new InetSocketAddress(Configure.DEFAULT_HOSTIP, Configure.DEFAULT_PORT);
			this.dgsocket.connect(address);
		} else {
			this.address = new InetSocketAddress(host, port);
			this.dgsocket.connect(address);
		}
		return this.dgsocket;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.loraiot.iot.comm.Connection#disconnect()
	 */
	@Override
	public boolean disconnect() throws IOException {
		// TODO Auto-generated method stub
		if (this.dgsocket != null) {
			dgsocket.close();
			this.dgsocket = null;
			return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.loraiot.iot.comm.Connection#putData(byte[])
	 */
	@Override
	public boolean putData(byte[] data) throws IOException{
		DatagramPacket packet = new DatagramPacket(data, data.length, Configure.getADDRESS(), Configure.port);  
		if ((this.dgsocket!=null) && (data != null)){
			this.dgsocket.send(packet);
			return true;
		}else{
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.loraiot.iot.comm.Connection#getData()
	 */
	@Override
	public byte[] getData() throws IOException{
			DatagramSocket ds = new DatagramSocket();          
			byte[] swap = new byte[2048];     
			DatagramPacket dp = new DatagramPacket(swap,0,swap.length);              
			byte[] buf = dp.getData();
			if (buf.length <= 0){
				System.out.println("read data length is "+buf.length);
				return null;
			}
			return buf;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.loraiot.iot.comm.Connection#isClosed()
	 */
	@Override
	public boolean isClosed(){
		if (this.dgsocket != null){
			return this.dgsocket.isClosed();
		}else{
			return true;
		}
	}
	
	
}
