package me.kvq.jdbridge;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class SocketRedirect {
	
	private static String ip;
	private static DatagramSocket udp;
	private static ServerSocket tcp;
	private static DatagramSocket udpsocketclient;
	private static UDPRouter udpserver;
	
	private static TCPRouter tcpservice;
	private static Socket tcpsocket, connection;
	
	public static ArrayList<TCPConnection> tcpconnections;
	
	static boolean running = false;
	
	public SocketRedirect(String ip) {
		if (ip == null || ip.replaceAll(" ", "").equalsIgnoreCase("")) {
			
			Status.update(Status.ERROR, "IP field is empty");
			
			return;
		}
		
		if (running) { System.exit(0); return;}
		
		tcpconnections = new ArrayList<>(); tcpconnections.ensureCapacity(64);
		
		SocketRedirect.ip = ip;
		if (udp != null || tcp != null) {
			Status.update(Status.ERROR, "Something went wrong, please restart JDBridge");
			return;
		}
		
		try {
			udp = new DatagramSocket(6000);
			udpserver = new UDPRouter(0);
			tcpservice = new TCPRouter(TCPType.SERVICE);
			
			Status.update(Status.WAIT);
			
			running = true; MainGui.getButton().setText("Stop and close");
			
		} catch (Exception e) {
			Status.update(Status.ERROR, e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public static void sendUDP(String ip) {
		try {
		String fake = "{\"platform\":\"PC\",\"titleId\":\"PC-JD2017\",\"protocol\":\"v1.phonescoring.jd.ubisoft.com\",\"consoleName\":\"PC (JDBridge "+ip+")\"}";
		DatagramPacket fa = new DatagramPacket(fake.getBytes(), fake.getBytes().length);
		DatagramSocket lastsocket = new DatagramSocket(6000);
		fa.setAddress(InetAddress.getByName(ip)); fa.setPort(7000);
		lastsocket.send(fa); lastsocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	class UDPRouter extends Thread{
		
		int type = -1; //0 = LocalToOnline; 1 = OnlineToLocal
		boolean on = true;
		
		InetAddress phoneip;
		InetAddress phoneport;
		
		
		public UDPRouter(int type) {
			this.type = type;
			if (type == 0) {try {
				udpsocketclient = new DatagramSocket();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
			this.start();
		}
		
		@Override
		public void run() {
			server();
		}
		
		public void server() {
			while(on) {
				
				DatagramSocket lastsocket;
				try {
					
				byte[] buffer = new byte[256];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				udp.receive(packet);
				
				
				InetAddress sourceip = packet.getAddress(); int sourceport = packet.getPort();
				
				Status.update(Status.UDPPING, " " + sourceip.getHostAddress() + ":" + sourceport);
				System.out.println(new String(packet.getData()));
				
				packet.setAddress(InetAddress.getByName(ip));
				packet.setPort(6000);
				
				String fake = "{\"platform\":\"PC\",\"titleId\":\"PC-JD2017\",\"protocol\":\"v1.phonescoring.jd.ubisoft.com\",\"consoleName\":\"PC (JDBridge "+ip+")\"}";
				DatagramPacket fa = new DatagramPacket(fake.getBytes(), fake.getBytes().length);

			
				lastsocket = new DatagramSocket();
				fa.setAddress(sourceip); fa.setPort(sourceport);
				lastsocket.send(fa); lastsocket.close();
				} catch (Exception e) {
					Status.update(Status.ERROR, e.getMessage());
					e.printStackTrace();
				} 
			}
		}
		

		public void shutdown() {
			on = false; udp.close();
		}
		

		
	}
	
	class TCPRouter extends Thread {
		
		
		TCPType type = TCPType.UNKNOWN; boolean on = true;
		
		
		
		public TCPRouter(TCPType type) {
			this.type = type;
			this.start();
		}
		
		Socket tcps; InputStream tcpin; OutputStream tcpout;
		public TCPRouter(Socket socket, InputStream in, OutputStream out) {
			this.type = TCPType.SERVERCLIENT;
			this.tcps = socket;
			this.tcpin = in;
			this.tcpout = out;
			this.start();
		}
		
		@Override
		public void run() {	
			if (type == TCPType.SERVICE) service();
			else if (type == TCPType.SERVERCLIENT) startRoute(tcps, tcpin, tcpout);
		}
		
		public void service() {
			try {
			tcp = new ServerSocket(8080);
			
				while (on) {
				tcpsocket = tcp.accept();	
				
				Status.update(Status.TCP);
				
				connection = new Socket(InetAddress.getByName(ip), 8080);
				tcpconnections.add(new TCPConnection(tcpsocket, 
						tcpsocket.getInputStream(), connection.getOutputStream(),
						connection.getInputStream(), tcpsocket.getOutputStream()));
				
				}
			} catch (Exception e) {
				Status.update(Status.ERROR, e.getMessage());
				e.printStackTrace();
			}
		}
		
		public void startRoute(Socket tcp,InputStream in, OutputStream out) {
			System.out.println("START" + Thread.currentThread().getId());
			while (!tcp.isClosed()) {
				
				try {
					copyy(in, out);
					System.out.println("END" + Thread.currentThread().getId());
				
//				out2.flush();
				out.flush();
				} catch (Exception e) {
					e.printStackTrace();
					Status.update(Status.ERROR, e.getMessage());
				}
			}
		}
		
		long dataout, datain;
		
		void copyy(InputStream source, OutputStream target) throws IOException {
		    byte[] buf = new byte[8192];
		    int length;
		    while ((length = source.read(buf)) > 0) {
		        target.write(buf, 0, length);
		    }
		}
		
		public void shutdown() {
			on = false; 
		}
		
		void copy(InputStream source, OutputStream target) throws IOException {
		    byte[] buf = new byte[8192];
		    int length;
		    System.out.println("Copy: ");
		    while ((length = source.read(buf)) >= 0) {
		    	System.out.print(" " + length);
		        target.write(buf, 0, length);
		    }
		}
		
		public byte[] getBytes(InputStream is) throws IOException {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[1024];
			
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				
			  buffer.write(data, 0, nRead);
			}

			return buffer.toByteArray();
		}
		
		public void killSC() {
			try {
				tcp.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private static final String    HEXES    = "0123456789ABCDEF";

		String getHex(byte[] raw) {
		    final StringBuilder hex = new StringBuilder(2 * raw.length);
		    for (final byte b : raw) {
		        hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
		    }
		    return hex.toString();
		}
		
		
	}
	
	enum TCPType {
		UNKNOWN,SERVICE,SERVER,CLIENT, SERVERCLIENT;
	}
	
	class TCPConnection{
		
		TCPRouter[] channels = new TCPRouter[2];
		Socket socket;
		public TCPConnection(Socket tcp0,TCPRouter tcp1, TCPRouter tcp2) {
			socket = tcp0;
			channels[0] = tcp1; channels[1] = tcp2;
		}
		
		public TCPConnection(Socket tcp0, InputStream in , OutputStream out, InputStream in2, OutputStream out2) {
			socket = tcp0;
			channels[0] = new TCPRouter(tcp0, in, out); 
			channels[1] = new TCPRouter(tcp0, in2, out2);
		}
		
		public TCPRouter getPhoneToServer() {
			return this.channels[0];
		}
		
		public TCPRouter getServerToPhone() {
			return this.channels[1];
		}

	}

}
