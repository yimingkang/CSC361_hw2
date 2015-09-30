import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
    public static void main(String[] args) throws IOException{
        int port_num = 9876;
        int basePort = port_num;
        int quit = 0;

        List<RunnableSocketWriter> threadsList = new ArrayList<RunnableSocketWriter>();

        System.out.println("Starting server at port " + port_num);
        ServerSocket serversSocket = new java.net.ServerSocket(port_num);
        
        while (true){
            Socket socket = serversSocket.accept();
            System.out.println("Server: Accepted connection"); 

            DataOutputStream writer = new DataOutputStream(socket.getOutputStream()); 
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader socket_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read filename
            String fileName= socket_reader.readLine();
            if(fileName == null){
                System.out.println("Server: Connection lost, closing control socket");
                socket.close();
                continue;
            }
            File file = new File(fileName);
            if (file.exists()){
                while(true){
                    try{
                        basePort++;
                        ServerSocket dataSocket = new java.net.ServerSocket(basePort);

                        // notify client of the port we're sending data to
                        System.out.println("Server: starting data port on port " + String.valueOf(basePort));
                        writer.writeBytes(String.valueOf(basePort));

                        // start writing from the socket as soon as a connection is accepted
                        RunnableSocketWriter sock_writer = new RunnableSocketWriter(dataSocket, fileName);
                        sock_writer.start();

                        // insert into arraylist so we can wait() on it later
                        threadsList.add(sock_writer);
                        break;
                    }
                    catch (Exception e) {
                        System.out.println("Server: port " + basePort + " is occupied!");
                    }
                }
            }
            else{
                System.out.println("Server: File " + fileName + " not found");
                writer.writeBytes("ERROR");
            }
            System.out.println("Server: Closing control socket"); 
            socket.close();
        }
    }
}
