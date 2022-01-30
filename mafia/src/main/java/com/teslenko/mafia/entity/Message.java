package com.teslenko.mafia.entity;

public class Message {
	String authorName;
	String msg;
	public Message(String authorName, String msg) {
		this.authorName = authorName;
		this.msg = msg;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
