import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Client extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIp;
	private Socket connection;
	
	//constructor 
	//crear el gui
	public Client(String host) {
		super("Client");
		serverIp = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}	
	 );
		add(userText,BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow),BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
		}
	//connect to server
	public void startRunning() {
		try {
			connectToServer();
			setUpStreams();
			whileChatting();
		}catch(EOFException eofException){
			showMessage("\n client terminated the conection");
			
		}catch (IOException ioException) {
			ioException.printStackTrace();
		}finally {
			closeCrap();
			
		}
		
	}
	//connect to server
	private void connectToServer() throws IOException{
		showMessage("Attempting connection...\n");
		//crea una conecion con tu ip adress y el port 
		//que es donde va a estar tu aplication
		connection = new Socket(InetAddress.getByName(serverIp),6789);
		showMessage("Connected to "+connection.getInetAddress().getHostName());
		}
	//set up Streams to send and receive message
	private void setUpStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream (connection.getInputStream());
		showMessage("\n you Stream are now good to go! \n");
		}
	
	//while chatting with server 
	private void whileChatting() throws IOException{
		ableToType(true);
		do {
			try {
				message = (String)input.readObject();
				showMessage("\n"+ message);
				
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n i dont know that object Type");
				}
			}while(!message.equals("SERVER - END"));
		}
	//close the stream and socket
	private void closeCrap() {
		showMessage("\n closing crap down");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException) {
			ioException.printStackTrace();
			
		}
	}
	//send message to the server
	private void sendMessage(String message) {
		try {
			output.writeObject("CLIENT - "+message);
			output.flush();
			showMessage("\n CLIENT - " + message);
			
		}catch(IOException ioException) {
			chatWindow.append("\n no se puedo enviar el mensaje");
		}
		
	}
	//change/update chatWindow
	private void showMessage(final String m) {
		SwingUtilities.invokeLater(
				new Runnable () {
					public void run() {
						chatWindow.append(m);
					}
				}
				
				);
		}
	//give the user permision to type into the box
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(
				new Runnable () {
					public void run() {
						userText.setEditable(tof);
					}
				}
				
				);
		
	}
	
	
	
	
	
	
	
}//end class
