package me.kvq.jdbridge;

import java.awt.Color;

import javax.swing.JLabel;

public enum Status {

	OFFLINE("Currently offline"), WAIT("Waiting for a phone"), UDPPING("Phone detected", new Color(0,128,0)), TCP("Routing data", new Color(0,128,0)), ERROR("Error: ", Color.RED), CUSTOM("");
	
	
	String msg = " ";
	Color color = Color.BLACK;
	
	Status(String message) {
		color = Color.BLACK;
		msg = message; 
	}
	
	Status(String message, Color c) {
		msg = message; color = c;
	}
	
	Status(){
		
	}
	
	public static void update(Status s, String info) {
		JLabel l = MainGui.getStatusText();
		l.setText(s.msg + (info == null ? "" : info)); l.setForeground(s.color);
	}
	
	public static void update(Status s) {
		update(s, null);
	}
}
