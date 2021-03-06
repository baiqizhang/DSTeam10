package clock;

import java.util.List;

import application.Log;
import message.Message;
import message.MessagePasser;
import message.TimestampedMessage;

public class VectorClockService extends ClockService {
	private int size;

	/**
	 * initializer
	 * @param size : count of nodes in the system
	 */
	public VectorClockService(int size) {
		this.timestamp = new VectorTimeStamp(size);
		this.size = size;
	};

	public VectorClockService(TimeStamp ts) {
		super(ts);
	}
		
	/**
	 * current timestamp getter
	 */
	@Override
	public TimeStamp getTimeStamp() {
		return this.timestamp;
	}

	/**
	 * update current time
	 * @param message a message that is just received/sent
	 * @return current time after the update
	 */

	@Override
	public TimestampedMessage addTimeStampToMessage(TimestampedMessage message) {
		List<String> names = MessagePasser.getAllNames();
		TimeStamp timeStamp = this.timestamp;
		if (timeStamp instanceof VectorTimeStamp){
			VectorTimeStamp vectorTimeStamp = (VectorTimeStamp)timeStamp;
			int index = names.indexOf(message.getSrc());
			vectorTimeStamp.incrementVectorItem(index);

			//set current time for clockservice
			this.timestamp = vectorTimeStamp;
			message.setTimeStamp(new VectorTimeStamp(vectorTimeStamp));
			return message;
		} else Log.error("VectorClockService", "timestamp type error");
		return null;
	}

	@Override
	public void ReceivedTimestampedMessage(TimestampedMessage timestampedMessage) {
		List<String> names = MessagePasser.getAllNames();
		// message received
		if (timestampedMessage.getDest().equals(MessagePasser.getLocalName())){
			TimeStamp timeStamp = timestampedMessage.getTimeStamp();
			if (timeStamp instanceof VectorTimeStamp){
				VectorTimeStamp receivedTimestamp = (VectorTimeStamp)timeStamp;				
				VectorTimeStamp myTimestamp = (VectorTimeStamp)this.timestamp;
				
				VectorTimeStamp resultTimetamp = new VectorTimeStamp(this.size);
				
				for (int index=0; index<size; index++){
					int myTime = myTimestamp.getVectorItem(index);
					int senderTime = receivedTimestamp.getVectorItem(index);
					resultTimetamp.setVectorItem(index, myTime>senderTime?myTime:senderTime);
				}
				int index = names.indexOf(timestampedMessage.getDest());
				resultTimetamp.incrementVectorItem(index);
				
				Log.info("VectorClockService", "ReceivedTimestampedMessage:"+timeStamp+"+"+myTimestamp+"->"+resultTimetamp);
				//set current time for clockservice
				this.timestamp = resultTimetamp;
			} else Log.error("VectorClockService", "timestamp type error");
		}
	}

	@Override
	public TimeStamp issueTimeStamp() {
		List<String> names = MessagePasser.getAllNames();
		TimeStamp timeStamp = this.timestamp;
		if (timeStamp instanceof VectorTimeStamp){
			VectorTimeStamp vectorTimeStamp = (VectorTimeStamp)timeStamp;
			int index = names.indexOf(MessagePasser.getLocalName());
			vectorTimeStamp.incrementVectorItem(index);

			//set current time for clockservice
			this.timestamp = vectorTimeStamp;
			Log.info("VectorClockService", "issue timestamp:"+timestamp.toString());
		} else Log.error("VectorClockService", "timestamp type error");
		return this.timestamp;
	}

}
