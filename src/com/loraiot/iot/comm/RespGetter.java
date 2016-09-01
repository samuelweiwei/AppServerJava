/**
 * 
 */
package com.loraiot.iot.comm;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.loraiot.iot.data.Parser;
import com.loraiot.iot.service.Configure;

/**
 * 
 * @author 10028484
 * @version 0.0.1
 */
public class RespGetter implements Runnable {

	private volatile String dataIncoming = null;

	private Socket socket = null;
	private byte[] message = null;
	private Connection conn = null;

	private volatile boolean runFlag = true;

	/**
	 * 
	 * 
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

	public void close() throws IOException {
		if (this.socket != null) {
			this.socket.close();
		}
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
		while (this.isRunFlag()) {
			if ((this.getConn() == null) || (this.getConn().isClosed())) {
				Configure.cmdseq_counter = Configure.DEFAULT_CMDSEQ;
				break;
			} else {
				try {
					this.message = this.getConn().getData();
					
					// decode and print
					if (this.message != null){
						String[] strMessage = Parser.parseRespBuf(this.message);
						System.out.println("answer is:" + new String(this.message));
					}else{
						System.out.println("read in message <= 0");						
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Normal Exception, can be beared,just keep go on");
					this.setRunFlag(false);					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch(NegativeArraySizeException e){
					System.out.println("connection closed");
					this.setRunFlag(false);
				}finally{
					try {
						this.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
