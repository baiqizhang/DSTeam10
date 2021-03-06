package message;

import org.yaml.snakeyaml.Yaml;

import application.Log;

import java.io.*;
import java.util.*;

@SuppressWarnings({ "rawtypes", "unchecked" })

public class Configuration {
	// Network configuration
	private List<Map> network;
	
	// Action Rules
	private List<Map> sendRules;
	private List<Map> receiveRules;
	
	public enum Action {
		Drop, DropAfter, Delay, NoAction
	}

	// Message directions
	public enum Direction {
		Send, Receive
	}

	// MARK: Constructors
	// ==============================================================
	
	public Configuration() {
		this("resources/config.yaml");
	}

	public Configuration(String filepath) {
		InputStream input;
		try {
			input = new FileInputStream(new File(filepath));
			Yaml yaml = new Yaml();

			Map config = (Map) yaml.load(input);
			network = (List<Map>) config.get("configuration");
			sendRules = (List<Map>) config.get("sendRules");
			receiveRules = (List<Map>) config.get("receiveRules");

//			System.out.println();
//			Log.verbose2("CONFIG", network.toString());
//			Log.verbose2("SEND", sendRules.toString());
//			Log.verbose2("RECEIVE", receiveRules.toString());
//			System.out.println();

		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			e.printStackTrace();
		}
	}

	// MARK: Getters
	// ==============================================================	

	/**
	 * @return a list of names defined in the network configuration file
	 */
	public List<String> getAllNames(){
		List<String> result = new LinkedList<String>();
		for (Map map : network){
			String name = (String) map.get("name");
			result.add(name);
		}
		return result;
	}
	
	/**
	 * @param name of the process
	 * @return String representation of the process's ip, null if not found
	 */
	public String getIpStringForName(String name){
		for (Map map : network){
			String dest = (String) map.get("name");
			if (name.equals(dest)){
				return (String) map.get("ip");
			}
		}
		return null;
	}
	
	/**
	 * @param name of the process
	 * @return Integer representation of the process's port, null if not found
	 */
	public Integer getPortNumberForName(String name){
		for (Map map : network){
			String dest = (String) map.get("name");
			if (name.equals(dest)){
				return (Integer) map.get("port");
			}
		}
		return null;
	}
	
	
	// Used by MessagePasser
	public Action getAction(Message message, Direction direction) {
		if (!message.isValid()) {
			System.err.println("message is not valid:" + message);
			return Action.Drop;
		}

		List<Map> rules = direction == Direction.Receive ? receiveRules : sendRules;

		for (Map rule : rules) {
			if (rule.get("src") != null && !message.getSrc().equals(rule.get("src")))
				continue;
			if (rule.get("dest") != null && !message.getDest().equals(rule.get("dest")))
				continue;
			if (rule.get("kind") != null && !message.getKind().equals(rule.get("kind")))
				continue;
			String action = (String) rule.get("action");
			Integer seqNum = (Integer)rule.get("seqNum");
			
			if (action.equals("dropAfter")) {
				if (seqNum != null && (int)message.getSeqNum() < (int)seqNum)
					continue;
				return Action.DropAfter;
			} else {
				if (seqNum != null && (int)message.getSeqNum() != (int)seqNum)
					continue;
				if (action.equals("drop"))
					return Action.Drop;
				if (action.equals("delay"))
					return Action.Delay;
			}
		}

		return Action.NoAction;
	}
}
