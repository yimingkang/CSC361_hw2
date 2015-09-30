import java.net.*;
import java.io.*;

class RunnableSocketReader implements Runnable {
    private Thread thisThread;
    private int socketNumber;
    private String fileName;

    RunnableSocketReader(int sNumber, String name){
        // Initialize with socket and fileName
        socketNumber = sNumber;
        fileName = name;
    }

    @Override 
    public void run() {
        File file=new File(fileName);
        if (file.exists()){
            System.out.println("Reader: Warning! " + fileName + " will be overwritten!");
        }

        try{ 
            // create socket
            System.out.println("Reader: Connecting to socket " + socketNumber + " to get file " + fileName);
			Socket socket = new Socket("localhost", socketNumber);
			DataInputStream socket_reader = new DataInputStream(socket.getInputStream());

            // create file
            FileOutputStream fout= new FileOutputStream(fileName);

            int total_bytes = 0;
            int len;
			byte[] buffer = new byte[1024];
            while(true){
                len = socket_reader.read(buffer);
                if(len < 0){
                    System.out.println("Reader: Finished downloading " + fileName);
                    break;
                }
                total_bytes += len;
                fout.write(buffer, 0, len);
            }
            System.out.println("Reader: Closing socket (" + total_bytes + " bytes)");
            socket.close();
            fout.close();
        }
        catch (Exception e) {e.getStackTrace();}
        
    }

    public void start(){
        if (thisThread == null){
            thisThread = new Thread(this, "ReaderThread");
            thisThread.start();
        }
    }

}
