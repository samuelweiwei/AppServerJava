package com.loraiot.iot.data;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loraiot.iot.data.datagram.CSIFResponse;
import com.loraiot.iot.service.Configure;

/**
 * The message parsing class, parse the message from byte array. 
 * @author 10028484
 * @version 0.0.1
 */
public class Parser {
	
	/**
	 * Google gson object to parse input bytes array to jason string.
	 */
	private Gson gson = null;
	
	
	/**
	 * Default constructor
	 */
	public Parser() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Parse the header of the message.
	 * @param header The header of the message in byte array.
	 * @return
	 */
	public int parseHeader(byte[] header) {
		int newheader = 0;
		int t;
		for (byte c : header) {
			t = Integer.parseInt(String.valueOf(c));
			newheader = newheader * 10 + t;
		}
		return newheader;
	}

	public CSIFResponse parseContent(String content) {
		gson = new GsonBuilder().disableHtmlEscaping().create();
		CSIFResponse resp = gson.fromJson(content, CSIFResponse.class);
		return resp;
	}

	/**
	 * Method to parse the message body in byte array.
	 * @param buf The message body in byte array.
	 * @return The Message body in String.
	 * @throws UnsupportedEncodingException
	 */
	public static String[] parseRespBuf(byte[] buf) throws UnsupportedEncodingException {
		// Check if the imcoming byte array is legal
		if ((buf == null) || (buf.length == 0)) {
			return null;
		}
		// Split the message and the head, body and different message s
		String whole = new String(buf, "UTF-8");
		String[] splitMessage = null;
		String[] swap = whole.split("\n");
		//Message include first and last two "
		splitMessage = new String[swap.length - 2];
		System.arraycopy(swap, 1, splitMessage, 0, swap.length - 2);
		int len = splitMessage.length;

		if (len > 0) {
			if (len % 2 == 0) {
				splitMessage = composeMesArray(splitMessage);
			}
		} else {
			// If an odd array, and first element is not the header, delete
			// the first
			if (splitMessage[0].length() > Configure.MAX_HEADER) {
				String[] newone = new String[len - 1];
				System.arraycopy(splitMessage, 1, newone, 0, len - 1);
				splitMessage = composeMesArray(newone);
			}
			// If an odd array, and last element is not the body, delete
			// the last
			else if (splitMessage[len - 1].length() < Configure.MAX_HEADER) {
				String[] newone = new String[len - 1];
				System.arraycopy(splitMessage, 1, newone, 0, len - 1);
				splitMessage = composeMesArray(newone);
			}
		}
		return splitMessage;
	}

	/**
	 * Method to delete the "\n" spliter in message start and end.
	 * @param origin The String of message whole.
	 * @return The String erased the head and end "\n".
	 */
	public static String deleteSpliter(String origin, String split) {
		if (origin == null) {
			return null;
		}
		while (origin.startsWith(split)) {
			origin = origin.substring(1, origin.length());
		}
		while (origin.endsWith(split)) {
			origin = origin.substring(0, origin.length() - 1);
		}
		return origin;
	}

	/**
	 * Method to compose the message String array, erasing the "\n".
	 * @param originMes The origin message string array.
	 * @return The string array erased of "\n".
	 */
	public static String[] composeMesArray(String[] originMes) {
		StringBuffer store = new StringBuffer();
		for (int i = 0; i < originMes.length; i++) {
			originMes[i] = deleteSpliter(originMes[i], "\n");
			originMes[i + 1] = deleteSpliter(originMes[i + 1], "\n");
			int j = Integer.parseInt(originMes[i]);
			if (originMes[i + 1].length() == j) {
				store.append(originMes[i + 1] + "\n");
			}
			i++;
		}
		String[] result = store.toString().split("\n");
		return result;
	}
}
