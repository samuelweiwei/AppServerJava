/**
 * 
 */
package com.loraiot.iot.service;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.loraiot.iot.comm.Connection;
import com.loraiot.iot.comm.ConnectionFactory;
import com.loraiot.iot.data.Encapsulator;
import com.loraiot.iot.data.datagram.CSData2Dev;
import com.loraiot.iot.data.datagram.CSJoinReq;
import com.loraiot.iot.data.datagram.CSQuit;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Parse the command line to send different message to CSIF, api interface is
 * also in the class.
 * 
 * @author 10028484
 * @version 0.0.1
 */
public class CLIParser {
	@Option(name = "-help", aliases = "-h", usage = "-appeui Setting the appeui of the login\n "
			+ "-nonce Setting the Device EUI\n -key Setting the enciphered key\n -pl Setting the payload data\n"
			+ "-deveui Setting the target deveui to send data -ip Setting the server IP to receive request"
			+ "-appkey Setting the 32 hex numbers' appkey -port Setting the server port to receive request"
			+ "-mport Setting the message key port", help = true)
	private String help;

	@Option(name = "-appeui", usage = "Setting the appeui of the login")
	private String appeui = Configure.DEFAULT_APPEUI;

	@Option(name = "-nonce", usage = "Setting the NONCE ")
	private String nonce = Configure.DEFAULT_NOPNCE;

	@Option(name = "-pl", usage = "Setting the payload data")
	private String payload = Configure.DEFAULT_PAYLOAD;

	@Option(name = "-deveui", usage = "Setting the target deveui to send data")
	private String deveui = Configure.DEFAULT_DEVEUI;

	@Option(name = "-appkey", usage = "Setting the 32 hex numbers' appkey")
	private String appkey = Configure.DEFAULT_APPKEY;

	@Option(name = "-port", usage = "Setting the server port to receive request")
	private String port = String.valueOf(Configure.DEFAULT_PORT);

	@Option(name = "-ip", usage = "Setting the server IP to receive request")
	private String ip = String.valueOf(Configure.DEFAULT_HOSTIP);

	@Option(name = "-mport", usage = "Setting the message key port")
	private String mport = String.valueOf(Configure.DEFAULT_MESSAGE_PORT);

	@Argument
	private List<String> arguments = new ArrayList<String>();

	// private static Logger logger = LogManager.getLogger();
	/**
	 * The input main command string.
	 */
	private static String cmd = null;

	/**
	 * Default constructor.
	 */
	public CLIParser() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Method to parse the command input in console
	 * 
	 * @param args
	 *            The input command line arguments.
	 * @return Byte array, the message byte array ready to put in socket.
	 * @throws Exception
	 */
	public byte[] parseCmd(String[] args) throws Exception {

		CmdLineParser parser = new CmdLineParser(this);

		try {
			// parse the input arguments which is a string array in args
			parser.parseArgument(args);
			if (arguments.isEmpty())
				throw new CmdLineException("No argument is given");

		} catch (CmdLineException e) {
			// if there's a problem in the command line,
			// you'll get this exception. this will report
			// an error message.
			System.err.println(e.getMessage());
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();
			return null;
		}
		// Acquire the main command like join, sento etc.
		cmd = args[0];
		Encapsulator caps = new Encapsulator();
		String cont = null, body = null;
		byte[] message = null, swap = null;
		switch (cmd.toUpperCase()) {
		case "CONF":
			if (!port.trim().equalsIgnoreCase(String.valueOf(Configure.DEFAULT_PORT))) {
				int portSwap = Integer.parseInt(port);
				if ((portSwap <= 0) || (portSwap > 65535)) {
					System.out.println("Illegal port");
					break;
				} else {
					Configure.port = portSwap;
				}
			}
			if (!ip.trim().equalsIgnoreCase(Configure.DEFAULT_HOSTIP)) {
				String ipcom = "([1-9]|[1-9][0-9]|1\\d\\d|2[0-4]\\d|25[0-5])\\."
						+ "([1-9]|[1-9][0-9]|1\\d\\d|2[0-4]\\d|25[0-5])\\."
						+ "([1-9]|[1-9][0-9]|1\\d\\d|2[0-4]\\d|25[0-5])\\."
						+ "([1-9]|[1-9][0-9]|1\\d\\d|2[0-4]\\d|25[0-5])";
				Pattern pattern = Pattern.compile(ipcom);
				Matcher mat = pattern.matcher(ip);
				boolean ipaddress = mat.find();
				if (ipaddress == false) {
					System.out.println("wrong ip address");
					break;
				} else {
					Configure.hostip = ip;
				}
			}
			MMCAgent.getThread().interrupt();
			InetSocketAddress address = new InetSocketAddress(Configure.hostip, Integer.valueOf(port));
			MMCAgent.setAdd(address.getAddress());
			Connection conn = ConnectionFactory.getConnect(address.getAddress(), Configure.port, "TCP");
			MMCAgent.getRg().setConn(conn);
			MMCAgent.setThread(new Thread(MMCAgent.getRg()));
			MMCAgent.getRg().setRunFlag(true);
			MMCAgent.getThread().start();
			break;
		case "JOIN":
			CSJoinReq jq = new CSJoinReq();
			/*
			 * if (appkey.trim().equals(Configure.DEFAULT_APPKEY) ){
			 * System.out.println("appkey must be supplied!"); break; }
			 */
			jq.setAppEUI(appeui);
			jq.setAppNonce(Integer.parseInt(nonce));
			jq.setChallenge(challenge(composeHexMsg(appeui, nonce), appkey));
			// System.out.println(jq.getChallenge().length());
			jq.setCmdSeq(Configure.cmdseq_counter);
			jq.setCMD(cmd.toUpperCase());
			body = Encapsulator.encapsulateContent(jq);
			System.out.println(body);
			jq.setHeader(Integer.toString(body.length()));
			jq.setContent(body);
			message = Encapsulator.composeMessage(body);
			break;
		case "QUIT":
			CSQuit quit = new CSQuit();
			quit.setAppEUI(appeui);
			quit.setCMD(cmd.toUpperCase());
			quit.setCmdSeq(Configure.cmdseq_counter);
			body = Encapsulator.encapsulateContent(quit);
			System.out.println(body);
			quit.setHeader(Integer.toString(body.length()));
			quit.setContent(body);
			message = Encapsulator.composeMessage(body);
			break;
		case "SENDTO":
			CSData2Dev cdata = new CSData2Dev();
			cdata.setAppEUI(appeui);
			cdata.setDevEUI(deveui);
			cdata.setCmdSeq(Configure.cmdseq_counter);
			cdata.setConfirm(true);
			// Deal with payload base64 encoding
			swap = payload.getBytes("UTF-8");
			String pltmp = CLIParser.encodeBase64(swap);
			// End deal with payload base64 encoding
			// System.out.println(pltmp);
			cdata.setPayload(pltmp);
			cdata.setCMD(cmd.toUpperCase());
			if (Integer.parseInt(mport) > 0) {
				cdata.setPort(Integer.parseInt(mport));
			}
			body = Encapsulator.encapsulateContent(cdata);
			System.out.println(body);
			cdata.setHeader(Integer.toString(body.length()));
			cdata.setContent(body);
			message = Encapsulator.composeMessage(body);
			break;
		default:
			break;
		}

		/*
		 * if (!appeui.equalsIgnoreCase(Configure.DEFAULT_APPEUI))
		 * System.out.println("appeui flag is set");
		 * 
		 * if (!nonce.equalsIgnoreCase(Configure.DEFAULT_NOPNCE))
		 * System.out.println("nonce flag is set");
		 * 
		 * if (!nonce.equalsIgnoreCase(Configure.DEFAULT_KEY))
		 * System.out.println("key flag is set");
		 * 
		 * if (!payload.equalsIgnoreCase(Configure.DEFAULT_PAYLOAD))
		 * System.out.println("payload data is set");
		 */

		// access non-option arguments
		// System.out.println("other arguments are:");
		// for (String s : arguments)
		// System.out.println(s);
		return message;

	}
	
	
	public static byte[] getAppkeyBytes(String appkey) throws UnsupportedEncodingException{
		if ((appkey == null) ||(appkey.trim().length() != 32)){
			return null;
		}
		byte[] appkeyBytes = new byte[16];
		char[] swap = appkey.toCharArray();
		int high,low;
		//UTF-9 is 3 bytes for one string character
		for(int i=0;i<swap.length;i++){
			high=Character.getNumericValue(swap[i]);
			low=Character.getNumericValue(swap[i++]);
			high = (high <<= 4);
			appkeyBytes[(i-1)/2] = (byte) ((high+low) & 0XFF);
		}
		
		return appkeyBytes;
	}

	/**
	 * Method to use AES 128 to compose the challenge parameter in join message
	 * @param content The content to use AES128 to encipher
	 * @param password The key to add on AES128 algorithm
	 * @return The string enciphered in AES128
	 * @throws UnsupportedEncodingException 
	 */
	public static String challenge(String content, String appkey) throws UnsupportedEncodingException {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			byte[] appkeybytes = getAppkeyBytes(appkey);
			if (appkeybytes == null){
				return null;
			}
			kgen.init(128, new SecureRandom(appkeybytes));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");// create cipher

			// Content from hex string to hex String
			int v;
			char[] cc = content.toCharArray();
			int[] newcb = new int[cc.length];
			byte[] fin = new byte[cc.length / 2];
			for (int i = 0; i < cc.length; i++) {
				newcb[i] = Character.getNumericValue(cc[i]);
				newcb[i + 1] = Character.getNumericValue(cc[i + 1]);
				newcb[i] = newcb[i] <<= 4;
				fin[i / 2] = (byte) ((newcb[i] + newcb[++i]) & 0xff);
				// System.out.print(fin[i/2]+" ");
				// int tmp = Integer.toHexString(i)

			}
			// System.out.println();
			cipher.init(Cipher.ENCRYPT_MODE, key);// initiate
			byte[] result = cipher.doFinal(fin);
			String str = new sun.misc.BASE64Encoder().encode(result);
			/*StringBuffer sb = new StringBuffer();
			int low, high;
			byte lowb, highb;
			for (int j = 0; j < result.length; j++) {
				low = Integer.valueOf((result[j] & 0x0f));
				high = Integer.valueOf(result[j] & 0xf0) >>> 4;
				lowb = intToByteArray(low)[3];
				highb = intToByteArray(high)[3];
				sb.append(Integer.toHexString(lowb));
				sb.append(Integer.toHexString(highb));
			}			
			System.out.println("challenge is:" + sb.toString());*/
			return (str); // encipher
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Transfer the incoming command line to standard hex message bytes[].
	 * @param appeui Appeui in messaage.
	 * @param nonce Nonce in message.
	 * @return The String has benn composed in HEX format
	 */
	public static String composeHexMsg(String appeui, String nonce) {
		String msg = null;
		if ((appeui == null) || (nonce == null)) {
			return msg;
		}
		if ((appeui.length() != Configure.APPEUI_LEN) || (nonce.length() != Configure.NONCE_LEN)) {
			return msg;
		}
		msg = appeui + nonce + "00000000";
		return msg;
	}

	/**
	 * Method to transfer the int into byte array, which is used to compose
	 * the message head.
	 * @param i The int stands for the length of the message.
	 * @return The byte array of the message header.
	 */
	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	/***
	 * Method to encode by Base64.
	 * @param input The input byte array to use BASE64 to transfer into the printable string.
	 * @return The printable string in base64 coder.
	 */
	public static String encodeBase64(byte[] input) throws Exception {
		return new sun.misc.BASE64Encoder().encode(input);
	}

	/***
	 * Method to decode by Base64.
	 * @param input The input String to use BASE64 to transfer into the byte array.
	 * @return The base 64 byte array.
	 */
	public static byte[] decodeBase64(String input) throws Exception {
		byte[] bt = null;
		try {
			sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
			bt = decoder.decodeBuffer(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bt;
	}

}
