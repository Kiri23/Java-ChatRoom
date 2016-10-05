import javax.swing.JFrame;
public class ClientTest {

	public static void main(String[] args) {
		Client charlie;
		//127.0.0.1 = locallhost
		//ahy es lo que necesito el ip adrees de un server
		//para ponerme conectar ahy con otras personas
		charlie = new Client("72.50.84.227");
		charlie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		charlie.startRunning();
	}

}
