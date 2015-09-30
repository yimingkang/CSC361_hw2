import java.io.*;
import java.net.*;


public class Server {
    public static void main(String[] args) throws IOException{
        int port_num = 9876;
        int quit = 0;
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
                System.out.println("Server: Connection lost....");
                break;
            }
            File file = new File(fileName);
            if (file.exists()){
                // Send the file here
                System.out.println("Server: File " + fileName + " exists");
                byte[] buffer = new byte[1024];
                FileInputStream fin= new FileInputStream(file);
                System.out.println("Server: Begin transmitting file");
                while(true){
                    int len = fin.read(buffer);
                    if (len<0){
                        // done writing here
                        break;
                    }else{
                        // write to socket
                        System.out.println("Server: Transmitting " + len + "bytes");
                        writer.write(buffer, 0, len);
                    }
                }
                System.out.println("Server: Done transmitting file");
            }
            else{
                System.out.println("Server: File " + fileName + " not found");
                writer.writeBytes("ERROR");
            }
            System.out.println("Server: Closing socket...");
            socket.close();
        }
    }
}
