package me.kvq.jdbridge;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class JDBridge {

	public static byte[] code =
			new byte[] {112 , 104 , 111 , 110 , 101 , 115 , 99 , 111 , 114 , 
					105 , 110 , 103 , 46 , 106 , 100 , 46 , 117 , 98 , 105 ,
					115 , 111 , 102 , 116 , 46 , 99 , 111 , 109};
	
	public static void main(String[] args) {
		
//		bind();
		
		
	}
	
	public static void bind() {
		try {
			DatagramSocket socket = new DatagramSocket(6000);
			
			DatagramSocket jdsocket = new DatagramSocket();
			System.out.println("Socket created");
			byte[] buffer = new byte[256];
			 
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			while (true) {
				socket.receive(request);
				bytesToHex(buffer);
				tobyte(buffer);
				
				DatagramPacket outrequest = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("localhost"), 6000);
				buffer = new byte[256];
				
				jdsocket.send(outrequest);
				jdsocket.receive(request);	
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void send() {
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			DatagramPacket outrequest = new DatagramPacket(code, code.length, InetAddress.getLocalHost(), 6000);
			byte[] buffer = new byte[64];
			 
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			
			clientSocket.send(outrequest);
			
			clientSocket.receive(request);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	public static void bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    System.out.println(hexChars);
	}
	
	public static void tobyte(byte[] bytes) {
		String b = "byte[] code = [";
		for (int i = 0; i < bytes.length; i++) {
			b = b + bytes[i];
			if (i < bytes.length - 1) { b = b + " , ";}
		}
		
		 System.out.println(b + "];");
	}
	
}
