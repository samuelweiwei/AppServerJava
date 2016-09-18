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
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// TODO Auto-generated method stub
		Configure.readConf();
		Scanner s = new Scanner(System.in);
		CLIParser cliparse = new CLIParser();
		byte[] data2send;
		connect();
		
		rg.setConn(conn);
		thread= new Thread(rg);
		thread.start();

		while (true) {
			if ((conn != null) && (!conn.isClosed())) {
				String line = s.nextLine();
				if (line.equals("exit")){
					break;
				}
				args = line.split(" ");
				data2send = cliparse.parseCmd(args);
				// server socket come data
				conn.putData(data2send);
				if (data2send != null) {
					Configure.cmdseq_counter = Configure.cmdseq_counter + 2;
				}
				Thread.sleep(2000);
				if (args[0].equalsIgnoreCase("quit")) {
					System.out.println("quit cmd sent");
					Configure.cmdseq_counter = Configure.DEFAULT_CMDSEQ;
					thread.interrupt();
					rg.setRunFlag(false);
					if ((conn != null) || (!conn.isClosed())) {
						conn.disconnect();
						conn = null;
					}

				}
			}
			if ((conn == null) || (conn.isClosed())) {
				conn = ConnectionFactory.getConnect(add, Configure.DEFAULT_PORT, Configure.comm_type);
				rg.setConn(conn);
				thread = new Thread(rg);
				rg.setRunFlag(true);
				thread.start();
				if (conn == null) {
					System.out.println("Acquire connection failed, connecting error");
					break;
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
	 * @throws IOException
	 */
	public static void connect() throws IOException{
		add = Configure.getADDRESS();
		conn = ConnectionFactory.getConnect(add, Configure.port, "TCP");
	}

	public static InetAddress getAdd() {
		return add;
	}

	public static void setAdd(InetAddress add) {
		MMCAgent.add = add;
	}
	
	public static void disconnect() throws IOException{
		if (conn != null){
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
