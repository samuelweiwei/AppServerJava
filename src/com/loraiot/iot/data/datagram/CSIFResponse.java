/**
 * 
 */
package com.loraiot.iot.data.datagram;

import java.io.Serializable;

import com.loraiot.iot.data.Message;

/**
 * The Message of CSIF response.
 * @author 10028484
 *
 */
public class CSIFResponse extends Message implements Serializable {
	
	private int code = 0;
	private String AppEUI = null;
	private String CMD = "";
	private int CmdSeq = 1;
	private String MSG= "";
	private String payload = "";
	private int port = 0;
	private String DevEUI = "";

	/**
	 * Default constructor
	 */
	public CSIFResponse() {
		// TODO Auto-generated constructor stub
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getAppEUI() {
		return AppEUI;
	}

	public void setAppEUI(String appEUI) {
		AppEUI = appEUI;
	}

	public String getCMD() {
		return CMD;
	}

	public void setCMD(String cMD) {
		CMD = cMD;
	}

	public int getCmdSeq() {
		return CmdSeq;
	}

	public void setCmdSeq(int cmdSeq) {
		CmdSeq = cmdSeq;
	}

	public String getMSG() {
		return MSG;
	}

	public void setMSG(String mSG) {
		MSG = mSG;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDevEUI() {
		return DevEUI;
	}

	public void setDevEUI(String devEUI) {
		DevEUI = devEUI;
	}

}
