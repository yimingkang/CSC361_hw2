import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class CodeFromClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// define a (client socket)
			Socket socket = new Socket("localhost", 1234);

			// read lines from socket:
			// define once:
			BufferedReader socket_bf = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			// use everytime!
			String str = socket_bf.readLine();

			// write lines to socket:
			// define once:
			DataOutputStream socket_dos = new DataOutputStream(
					socket.getOutputStream());
			String CRLF = "\r\n";
			// use everytime to write a line:
			socket_dos.writeBytes("hello world!" + CRLF);

			// /////new stuff:

			// write data to socket:
			byte[] buffer = new byte[1024];
			socket_dos.write(buffer);
			// write a portion of buffer:
			socket_dos.write(buffer, 0, 1000);

			// read data from socket
			// define once:
			DataInputStream socket_dis = new DataInputStream(
					socket.getInputStream());
			
			int len =socket_dis.read(buffer);
			if(len<0)
				System.out.println("done receiving data.");
			
			//defining a file with a specific name and checking if it exists:
			File file=new File("filename");
			
			if (file.exists())
			{}
				//server responds YES
			else
			{}
				//reserver responds NO
				
			
			//reading data from file:
			//define once:
			FileInputStream fis= new FileInputStream(file);
			//use everytime:
			len= fis.read(buffer);
			if (len<0)
				; //that means you have reached the end of file.
			
			
			//writing data to file:
			//define once:
			FileOutputStream fos= new FileOutputStream(file);
			//use everytime:
			fos.write(buffer); //writing the whole buffer
			fos.write(buffer, 0, len); //writing a portion of buffer
			
			fos.close();
			fis.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
