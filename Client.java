import java.net.*;
import java.io.*;

public class Client{
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Hello World");
		try{
			
			Socket socket = new Socket("localhost",9876);
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

			
            String str = reader.readLine();
            writer.writeBytes(str + "\r\n");

            // start reading from the socket as soon as a connection is accepted
            RunnableSocketReader sock_reader = new RunnableSocketReader(socket, str);
            sock_reader.start();

            // must wait for thread to finish
            sock_reader.wait();
            
		}catch(Exception e){e.getStackTrace();}
	}
}

