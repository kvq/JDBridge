package me.kvq.jdbridge;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Window.Type;

public class MainGui {

	private JFrame frmJdBridgeFor;
	private JTextField txtF;
	
	private static JLabel StatusText = null;
	private static JButton btnNewButton = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGui window = new MainGui();
					window.frmJdBridgeFor.setVisible(true);
					Status.update(Status.OFFLINE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmJdBridgeFor = new JFrame();
		frmJdBridgeFor.setTitle("JDBridge for Just Dance 2017");
		frmJdBridgeFor.setResizable(false);
		frmJdBridgeFor.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmJdBridgeFor.setBounds(100, 100, 450, 250);
		frmJdBridgeFor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmJdBridgeFor.getContentPane().setLayout(null);
		
		txtF = new JTextField();
		txtF.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtF.setBounds(35, 52, 357, 45);
		frmJdBridgeFor.getContentPane().add(txtF);
		txtF.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Host IP address");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel.setBounds(35, 13, 262, 30);
		frmJdBridgeFor.getContentPane().add(lblNewLabel);
		
		btnNewButton = new JButton("Start Routing");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				new SocketRedirect(txtF.getText());
				
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnNewButton.setBounds(65, 110, 304, 40);
		frmJdBridgeFor.getContentPane().add(btnNewButton);
		
		StatusText = new JLabel("STATUS_MSG");
		StatusText.setHorizontalAlignment(SwingConstants.CENTER);
		StatusText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		StatusText.setBounds(12, 163, 408, 40);
		frmJdBridgeFor.getContentPane().add(StatusText);
	}
	
	public static JButton getButton() {
		return btnNewButton;
	}
	
	public static JLabel getStatusText() {
		return StatusText;
	}
}
