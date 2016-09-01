package com.loraiot.iot.data;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The message encapsulating class.
 * @author 10028484
 * @version 0.0.1
 */
public class Encapsulator {
	
	/**
	 * Google gson object to encapsulate gson string for input bytes array.
	 */
	private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	/**
	 * Default constructor.
	 */
	public Encapsulator() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Method to encapsulate the messaage header.
	 * @param header The int header value.
	 * @return The header string tranfered from int.
	 */
	public static String encapsulateHeader(int header){
		String newheader = null;
		if(header<=0 || header>2048){
			return null;
		}
		newheader = Integer.toString(header);
		return newheader;
	}
	
	/**
	 * Method to encapsulate jason string from Object by google gson.
	 * @param content The message bean
	 * @return The jason string
	 * @throws UnsupportedEncodingException 
	 */
	public static String encapsulateContent(Object content) throws UnsupportedEncodingException{
			return gson.toJson(content);		
	}
	
	/**
	 * Method to compose the string body to UTF-8 byte array, ready to put in the socket.
	 * @param body The message body string.
	 * @return The message byte array coded in UTF-8.
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] composeMessage(String body) throws UnsupportedEncodingException{
		int len = body.length();
		String head = Encapsulator.encapsulateHeader(len);
		StringBuffer sb = new StringBuffer();
		sb.append(head);
		sb.append("\n");
		sb.append(body);
		sb.append("\n");
		return sb.toString().getBytes("UTF-8");
	}

}
