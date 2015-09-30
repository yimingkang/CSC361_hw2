import java.net.*;
import java.io.*;

class RunnableSocketWriter implements Runnable {
    private Thread thisThread;
    private ServerSocket serverSocket;
    private String fileName;

    RunnableSocketWriter(ServerSocket soc, String name){
        // Initialize with socket and fileName
        serverSocket = soc;
        fileName = name;
    }

    @Override 
    public void run() {
        File file=new File(fileName);

        try{ 
            Socket socket = serverSocket.accept();
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream()); 
            System.out.println("Writer: Established connection for file " + fileName);

            // Send the file here
            byte[] buffer = new byte[1024];
            FileInputStream fin= new FileInputStream(file);
            System.out.println("Writer: Begin transmitting file");
            int total_bytes = 0;

            while(true){
                int len = fin.read(buffer);
                if (len<0){
                    // done writing here
                    break;
                }else{
                    // write to socket
                    writer.write(buffer, 0, len);
                    total_bytes += len;
                }
            }
            System.out.println("Writer: Done transmitting file (" + total_bytes + " bytes)");
            socket.close();
            fin.close();
        }
        catch (Exception e) {e.getStackTrace();}
        
    }

    public void start(){
        if (thisThread == null){
            thisThread = new Thread(this, "WriterThread");
            thisThread.start();
        }
    }

}
