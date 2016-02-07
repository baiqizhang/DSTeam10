package test;

import java.util.*;

import application.LogEntry;
import application.Logger;
import clock.ClockService;
import clock.ClockServiceFactory;
import clock.TimeStamp;
import message.Broker;
import message.Message;
import message.MessagePasser;

public class TestLogger {

	public static void  main(String[] args) throws InterruptedException {
		// Read arguments
		if(args.length != 3) {
			System.out.println("Usage : TestDriver <config file path> <Local Name> <Clock Service Type>");
			System.exit(1);
		}

		// init MessagePasser, read configuration file and start listening
		System.out.println("Welcome to team22-lab1");

		// TODO : clean this mess, all sorts of init just make no sense
		ClockServiceFactory.setClockService(args[2],4);
		MessagePasser.init(args[0], args[1]);
		Broker broker = new Broker();
		Logger logger = new Logger(args[0], args[1]);
		broker.register("Logger", logger);
		
		// simple UI
		boolean done = false;
		while (!done) {
			Thread.sleep(300);
			System.out.println("=======================");
			System.out.println("1. send a log");
			System.out.println("2. dump all logs");
			System.out.println("3. show timestamp");
			System.out.println("other number: exit");
			System.out.println("=======================");
			Scanner scanner = new Scanner(System.in);

			try {
				int selection = scanner.nextInt();
				Message message;
				switch (selection) {
				case 1:
					System.out.print("destination(name):");
					String dest = scanner.next();
					System.out.print("kind:");
					String kind = scanner.next();
					System.out.print("logcontent:");
					String payload = scanner.next();
					LogEntry logEntry = new LogEntry(payload);
					message = new Message(dest, kind, logEntry);
					MessagePasser.send(message);
				case 2:
					System.out.println("Current log file is as follows!");
					logger.dumpLog();
					break;
				case 3:
					TimeStamp ts = ClockServiceFactory.getClockService().getTimeStamp();
					System.out.println("Current timeStamp is" + ts);
					break;
				default:
					MessagePasser.terminateAll();
					return;
				}				
			} catch (InputMismatchException e) {
				System.out.println("INPUT MISMATCH!");
			}
		}
	}

}