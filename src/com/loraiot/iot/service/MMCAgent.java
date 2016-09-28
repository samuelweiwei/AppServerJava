/**
 * 
 */
package com.loraiot.iot.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import com.loraiot.iot.comm.Connection;
import com.loraiot.iot.comm.ConnectionFactory;
import com.loraiot.iot.comm.RespGetter;

/**
 * 
 * @author 10028484
 * @version 0.0.1
 */
public class MMCAgent {

	private static Connection conn;

	private static InetAddress add;

	private static Thread thread;

	private static RespGetter rg = new RespGetter();

	/**
	 * Defautl constructor.
	 */
	public MMCAgent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Entry of whole process
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// TODO Auto-generated method stub
		Configure.readConf();
		Scanner s = new Scanner(System.in);
		CLIParser cliparse = new CLIParser();
		byte[] data2send;
		try {
			add = Configure.getADDRESS();
			connect();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		rg.setConn(conn);
		thread = new Thread(rg);
		thread.start();

		while (true) {
			String line = s.nextLine();
			if (line.equals("exit")) {
				if ((conn != null) && (!conn.isClosed())) {
					conn.disconnect();
					conn = null;
				} else {
					conn = null;
				}
				rg.setConn(conn);
				thread.interrupt();
				break;
			}
			args = line.split(" ");
			data2send = cliparse.parseCmd(args);
			if (!args[0].trim().equalsIgnoreCase("quit")) {
				if ((conn == null) || (conn.isClosed())) {
					conn = connect();
					rg = new RespGetter();
					rg.setConn(conn);
					thread = new Thread(rg);
					thread.start();

				}
			}
			if ((conn != null) && (!conn.isClosed())) {
				// server socket come data
				try {
					conn.putData(data2send);
				} catch (IOException e) {
					if ((conn == null) || (conn.isClosed())) {
						InetAddress add;
						try {
							add = Configure.getADDRESS();
							conn = ConnectionFactory.getConnect(add, Configure.port, "TCP");
							rg.setConn(conn);
							conn.putData(data2send);
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							System.out.println(
									"retry connect fail or connection problem, break out and restart the applicatoin");
							e1.printStackTrace();
							thread.interrupt();
							rg.setRunFlag(false);
							thread.interrupt();
							if ((conn != null) || (!conn.isClosed())) {
								conn.disconnect();
								conn = null;
								rg.setConn(null);
							}
							break;
						}
					}
				}
				if (data2send != null) {
					Configure.cmdseq_counter = Configure.cmdseq_counter + 2;
				}
				Thread.sleep(2000);
				if (args[0].trim().equalsIgnoreCase("quit")) {
					System.out.println("quit cmd has sent");
					Configure.cmdseq_counter = Configure.DEFAULT_CMDSEQ;
					rg.setRunFlag(false);
					thread.interrupt();
					// thread.interrupt();
					if ((conn != null) || (!conn.isClosed())) {
						conn.disconnect();
						conn = null;
						rg.setConn(null);
					}
				}
				
				//In fact, if application runs, the connection must be kept, if closed, open again
				if ((conn == null) || (conn.isClosed())) {
					conn = connect();
					rg.setConn(conn);
					thread = new Thread(rg);
					rg.setRunFlag(true);
					thread.start();
					if (conn == null) {
						System.out.println("Acquire connection failed, connecting error");
						break;
					}
				}
			}

			/*
			 * if (rg.getMessage() != null){ System.out.println("answer is:"+new
			 * String(rg.getMessage())); }
			 */
			// /System.out.println(">>>" + line);

		}

	}

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		MMCAgent.conn = conn;
	}

	/**
	 * Connect the socket to server.
	 * 
	 * @throws IOException
	 */
	public static Connection connect() throws IOException {
		add = Configure.getADDRESS();
		conn = ConnectionFactory.getConnect(add, Configure.port, "TCP");
		return conn;
	}

	public static InetAddress getAdd() {
		return add;
	}

	public static void setAdd(InetAddress add) {
		MMCAgent.add = add;
	}

	public static void disconnect() throws IOException {
		if (conn != null) {
			conn.disconnect();
		}
	}

	public static Thread getThread() {
		return thread;
	}

	public static void setThread(Thread thread) {
		MMCAgent.thread = thread;
	}

	public static RespGetter getRg() {
		return rg;
	}

	public static void setRg(RespGetter rg) {
		MMCAgent.rg = rg;
	}
}
