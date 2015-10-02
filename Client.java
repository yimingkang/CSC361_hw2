import java.net.*;
import java.io.*;
import java.util.*;

public class Client{
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Hello World");
        String str;
        String response;
		while(true){
			Socket socket = new Socket("localhost",9876);
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader socket_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            List<RunnableSocketReader> readerThreads = new ArrayList<RunnableSocketReader>();

            str = reader.readLine();
            writer.writeBytes(str + "\r\n");

            response = socket_reader.readLine();
            if (response.compareTo("ERROR") == 0){
                System.out.println("Client: File " + str + " not found!");
                socket.close();
                continue;
            }

            int socketNumber = Integer.parseInt(response);
            System.out.println("Client: Server opened port " + socketNumber);

            // start reading from the socket as soon as a connection is accepted
            RunnableSocketReader sock_reader = new RunnableSocketReader(socketNumber, str, "localhost"); 
            sock_reader.start();

            // add to readerTheads to wait() later
            readerThreads.add(sock_reader);
            socket.close();
		}
	}
}

