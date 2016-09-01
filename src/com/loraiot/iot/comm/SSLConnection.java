package com.loraiot.iot.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import com.loraiot.iot.service.Configure;

/**
 * The SSL connection to CSIF server.
 * @author 10028484
 * @version 0.0.1
 */
public class SSLConnection implements Connection {

	/**
	 * The ssl facotory to construct ssl socket by default.
	 */
	private SSLSocketFactory sslfactory = null;
	
	/**
	 * The internal ssl socket to connect to CSIF.
	 */
	private SSLSocket sslsocket = null;
	
	/**
	 * The host CSIF server Socket address object.
	 */
	private SocketAddress address = null;
	
	
	/**
	 * Default constructor
	 */
	public SSLConnection() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see com.loraiot.iot.comm.Connection#getConnection(java.net.InetAddress, int)
	 */
	@Override
	public SSLSocket getConnection(InetAddress host, int port) throws IOException {
		// TODO Auto-generated method stub
		this.sslfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		this.sslsocket = (SSLSocket) this.sslfactory.createSocket(host, port);
		if ((address == null) || (port <= 0 || port > 65535)) {
			this.address = new InetSocketAddress(Configure.hostip, Configure.port);
			this.sslsocket.connect(address);
		} else {
			address = new InetSocketAddress(host, port);
			this.sslsocket.connect(address);
		}
		return this.sslsocket;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.loraiot.iot.comm.Connection#disconnect()
	 */
	@Override
	public boolean disconnect() throws IOException {
		// TODO Auto-generated method stub
		if (this.sslsocket != null) {
			this.sslsocket.close();
			this.sslsocket = null;
			return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.loraiot.iot.comm.Connection#putData(byte[])
	 */
	@Override
	public boolean putData(byte[] data) throws IOException {
		if ((this.sslsocket != null) && (data != null)) {
			OutputStream os = this.sslsocket.getOutputStream();
			os.write(data);
			os.flush();
			return true;
		} else {
			return false;
		}

	}
	
	/*
	 * (non-Javadoc)
	 * @see com.loraiot.iot.comm.Connection#getData()
	 */
	@Override
	public byte[] getData() throws IOException{
		if (this.sslsocket != null) {
			InputStream is = this.sslsocket.getInputStream();
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
	 * @see com.loraiot.iot.comm.Connection#isClosed()
	 */
	@Override
	public boolean isClosed(){
		if (this.sslsocket != null){
			return this.sslsocket.isClosed();
		}else{
			return true;
		}
	}

}
