/**
 * 
 */
package com.loraiot.iot.data.datagram;

import java.io.Serializable;

import com.loraiot.iot.data.Message;
import com.loraiot.iot.service.Configure;

/**
 * The message of CS to set the mot device status.
 * @author 10028484
 * @version 0.0.1
 */
public class CSSetDevMod extends Message implements Serializable {
	
	private String CMD = "CLASS";
	private String AppEUI = Configure.DEFAULT_APPEUI;
	private int CmdSeq = Configure.DEFAULT_CMDSEQ;
	private String DevEUI = Configure.DEFAULT_DEVEUI;
	private String CLASS = "A";

	/**
	 * Default constructor.
	 */
	public CSSetDevMod() {
		// TODO Auto-generated constructor stub
	}

	public String getCMD() {
		return CMD;
	}

	public void setCMD(String cMD) {
		CMD = cMD;
	}

	public String getAppEUI() {
		return AppEUI;
	}

	public void setAppEUI(String appEUI) {
		AppEUI = appEUI;
	}

	public int getCmdSeq() {
		return CmdSeq;
	}

	public void setCmdSeq(int cmdSeq) {
		CmdSeq = cmdSeq;
	}

	public String getDevEUI() {
		return DevEUI;
	}

	public void setDevEUI(String devEUI) {
		DevEUI = devEUI;
	}

	public String getCLASS() {
		return CLASS;
	}

	public void setCLASS(String cLASS) {
		CLASS = cLASS;
	}

}
