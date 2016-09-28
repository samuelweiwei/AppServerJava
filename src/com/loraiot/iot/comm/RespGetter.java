/**
 * 
 */
package com.loraiot.iot.comm;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.loraiot.iot.data.Parser;
import com.loraiot.iot.service.Configure;
import com.loraiot.iot.service.MMCAgent;

/**
 * 
 * @author 10028484
 * @version 0.0.1
 */
public class RespGetter implements Runnable {

	private volatile String dataIncoming = null;

	private byte[] message = null;
	private Connection conn = null;

	private volatile boolean runFlag = true;

	/**
	 * Default Constructor 
	 */
	public RespGetter() {
		// TODO Auto-generated constructor stub
	}

	public void GetterServerStart() {
	}

	public String getDataIncoming() {
		return dataIncoming;
	}

	public void setDataIncoming(String dataIncoming) {
		this.dataIncoming = dataIncoming;
	}


	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public void run() {
		this.message = new byte[2048];
		// Check if RespGetter is for running
		while (this.isRunFlag()) {
			// If the connection is closed or is null, reconnect for ready send
			// or receive message
			if ((this.getConn() == null) || (this.getConn().isClosed())) {
				Configure.cmdseq_counter = Configure.DEFAULT_CMDSEQ;
				InetAddress add;
				try {
					add = Configure.getADDRESS();
					this.conn = ConnectionFactory.getConnect(add, Configure.port, "TCP");
					MMCAgent.setConn(this.conn);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					this.message = this.getConn().getData();

					// decode and print
					if (this.message != null) {
						String[] strMessage = Parser.parseRespBuf(this.message);
						System.out.println("answer is:" + new String(this.message));
					} else {
						System.out.println("read in message <= 0");
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					System.out.println("Normal Interrupted Exception, can be beared,just keep go on");
					// this.setRunFlag(false);
					// Configure.cmdseq_counter=Configure.DEFAULT_CMDSEQ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if ((this.getConn() == null) || (this.getConn().isClosed())) {
						InetAddress add;
						try {
							add = Configure.getADDRESS();
							this.conn = ConnectionFactory.getConnect(add, Configure.port, "TCP");
							MMCAgent.setConn(this.getConn());
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				} catch (NegativeArraySizeException e) {
					System.out.println("connection closed");
					this.setRunFlag(false);
					Configure.cmdseq_counter = Configure.DEFAULT_CMDSEQ;
				} finally {					
					/*if (!this.isRunFlag()) {
						try {
							this.getConn().disconnect();
							this.setConn(null);
							System.out.println("End the application, if require using it, please restart it!");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}*/
				}

			}
		}
	}

	public boolean isRunFlag() {
		return runFlag;
	}

	public void setRunFlag(boolean runFlag) {
		this.runFlag = runFlag;
	}

}
