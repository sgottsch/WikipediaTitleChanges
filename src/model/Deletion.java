package model;

import java.util.Date;

public class Deletion {

	private String source;

	private Date timeStamp;

	public Deletion(String source, Date timeStamp) {
		super();
		this.source = source;
		this.timeStamp = timeStamp;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

}
