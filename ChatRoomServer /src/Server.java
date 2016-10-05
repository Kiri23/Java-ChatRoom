import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.Border;

public class Server extends JFrame{
	// esta va a ser el area donde el user escribe sus mensaje 
	private JTextField userText;
	//donde se van a ver los mensajes del user y el cliente
	private JTextArea chatWindow;
	//este es el string ,el mesaje que tu le envias al companero 
	//se llama out pq tu lo envias ...si se llama in tu lo recibe
	private ObjectOutputStream output;
	//este se llama in so este es el mensaje que yo recibo
	private ObjectInputStream input;
	//este es el server en el que yo tengo que 
	//esperar que la gente se conecte a este server
	//en el server tienes que ponerle el port ,ip,
	//literalmente"creo que es lo mas imp".
	private ServerSocket server; 
	//this is the connection... socket es = conecttion
	//y esta tambn es BN IMPORTANTE
	//set up the socket es literalmente 
	//setea la conecion entre tu y el cliente together.
	private Socket connection;
	
	//constructor 
	public Server() {
		super("Kiri Instant Messenger");
		userText = new JTextField();
		//set editable to false pq 
		//no hace sentido escribir algo 
		//cuando no hay nadie conectado
		userText.setEditable(false);
		//se le pone un acctionListener para 
		//cuando el user le de enter para enviar el mensaje
		//ocurra algo
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						sendMessage(event.getActionCommand());
						//cuando le das enter borra el mensaje 
						//que le escribiste
						userText.setText(" ");
					}
				}
				
				);
		add(userText,BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
		
	}//end constructor
	
	//set up and run the server
	public void startRunning() {
		try {
			// primer paramtro port. 
			//segundo parametro es backlog que es cuantas persona yo quiero 
			//que esten conectados
			server = new ServerSocket(6789, 100);
			// esto se repite siempre over and over again
			while(true) {
				try {
					//wait for someone to connect
					// cuando al fin se conecta alguien es que crea 
					// una conecion osea una variable socket
					waitForConnection();
					//set up a conection entres las dos computadora 
					//o lo que le llaman un stream 
					setupStreams();
					//mientras dos presonas estan conectadas que puedan tener 
					//una conversacion
					// este metod es pa tener una conversacion
					whileChatting();
				}catch(EOFException eofException) {
					//termina la conversacion.." esta persona ya no esta disponible"
					showMessage("\n Server ended the connection!");
				}finally {
					//esto termin o borra los mensajes y la conecion etc.
					closeCrap();
				}
				
			}
			
			
		}catch(IOException ioException) {
			ioException.printStackTrace();
			
		}
		
	}
	//wait for connection ,then display connection information
	private void waitForConnection() throws IOException{
		showMessage(" Waiting for someone to connect...\n");
		//cuando alguien se conecte es que va a crear el socket.
		// no es que crea mil empty socket y despues tiene una conecioon con alguien
		connection = server.accept();
		//convierte tu dirrecion ip en string
		showMessage(" now connected to "+ connection.getInetAddress().getHostName());
		}
	//get stream to send and receive date .
	// stream = connection.
	private void setupStreams() throws IOException{
		// el output y el input le escribe a una conecion  //
		output = new ObjectOutputStream(connection.getOutputStream());
		//only you can flush your toilet ..
		// no puedes ir a la casa del otro y flush su toilet.. su informacion
		//por eso solamente output hace flush y no input.. pq inpu viene de ellos y ya ellos 
		//han hecho flush
		// esto es como limpiar la tuberia de informacion antes de enviar un mensaje
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Stream are now setup! \n");
		
	}
	// durring the chat conversation
	private void whileChatting() throws IOException{
		String message = "You are now connected! ";
		sendMessage(message);
		//allow the user to type
		ableToType(true);
		//como ya estas conectado 
		//y ya setiaste la coneccion
		//necesitas que aoaresca por primera vez el chat o la conversacion 
		do {
			try {
				// esto va cojer lo que cualquiera pusiera en su input 
				// y lo va a convertir en una string y lo pones en message 
				//luego lo mostra.
				message = (String)input.readObject();
				showMessage("\n "+message);
			}catch(ClassNotFoundException classNotFoundException) {
				// esto se usa si el sender envia algo que no sea string.
				showMessage("\n i dont know what the user sent! ");
			}
			
		}// para parar la conversacion..sentinela
		while(!message.equalsIgnoreCase("CLIENT - END"));
		
	}
	//close string and socket after you are done chatting
	public void closeCrap() {
		showMessage("\n closing connections... /n");
		ableToType(false);
		try {
			output.close();
			input.close();
			//this close the connection
			connection.close();
			
		}catch(IOException ioException) {
			ioException.printStackTrace();
			
		}
	}
	//send a message to the client
	private void sendMessage(String message) {
		try {
			//este metodo  se crea priemro en el listener 
			//el listener que es getAcctionComand-- devuelve 
			//un string y ese string lo coje el output por eso el 
			//write object
			// ahora el output tiene el message
			
			output.writeObject("SERVER-"+message);
			output.flush();
			// dispolay the history of the conversation
			showMessage("\n SERVER- "+ message);
			
		}catch(IOException ioException) {
			chatWindow.append("\n Error: DUDE I CANT SEND THAT MESSAGE ");
			
		}
		
	}
	// update chatWindow
	private void showMessage(final String text) {
		// en vez de crear un nuevo window 
		//lo que hace es darle update por eso el thread
		// y el thread le da update cada ves 
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						chatWindow.append(text);
						
					}
					
				}
				
				);
		}
	//let the user type stuf into their box 
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						userText.setEditable(tof);
						
					}
					
				}
				
				);
		
	}
	
	
	
}//end class Server
