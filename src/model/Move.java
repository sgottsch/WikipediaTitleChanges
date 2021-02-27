package model;

import java.util.Date;

public class Move {

	private String source;

	private String target;

	private Date timeStamp;

	public Move(String source, String target, Date timeStamp) {
		super();
		this.source = source;
		this.target = target;
		this.timeStamp = timeStamp;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String toString() {
		return this.source + "\t" + this.target + "\t" + this.timeStamp;
	}

}
