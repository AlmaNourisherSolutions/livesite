package com.alma.exception;

public class CustomGenericException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String errCode;
	private String errMsg;

	public String getErrCode() {
		return this.errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return this.errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public CustomGenericException(String errCode, String errMsg) {
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

	public CustomGenericException(String errMsg) {
		this.errMsg = errMsg;
	}
}
