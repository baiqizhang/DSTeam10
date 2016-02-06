package clock;

import java.util.ArrayList;
import java.util.List;

import driver.Log;

public class LogicalTimeStamp extends TimeStamp{

	private Integer logicalTime = 0;
	
	public Integer getLogicalTime() {
		return this.logicalTime;
	}
	
	public void setLogicalTime(Integer lt) {
		this.logicalTime = lt;
	}
	@Override
	public int compareTo(Object o) {
		if (o instanceof LogicalTimeStamp){
			//get vector of the other timestamp
			LogicalTimeStamp logicalTimeStamp = (LogicalTimeStamp)o;			
			Integer otherTime = logicalTimeStamp.logicalTime;
			
			return logicalTime.compareTo(otherTime);
		} else throw new ClassCastException();
	}

}
