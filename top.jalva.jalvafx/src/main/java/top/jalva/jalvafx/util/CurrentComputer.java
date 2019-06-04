package top.jalva.jalvafx.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CurrentComputer {

	public static String getName(){
		String hostname = "";
		
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();	
		} catch (UnknownHostException e) {}

		return hostname;		
	}
}
