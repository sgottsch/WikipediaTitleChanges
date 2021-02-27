package query;

import java.util.Date;

public class TitleChangeQuery {

	private String currentName;

	private Date currentDate;

	private Date targetDate;

	public TitleChangeQuery(String currentName, Date currentDate, Date targetDate) {
		super();
		this.currentName = currentName;
		this.currentDate = currentDate;
		this.targetDate = targetDate;
	}

	public String getCurrentName() {
		return currentName;
	}

	public void setCurrentName(String currentName) {
		this.currentName = currentName;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public Date getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

}
