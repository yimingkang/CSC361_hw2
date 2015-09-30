import java.net.*;
import java.io.*;

class RunnableSocketReader implements Runnable {
    private Thread thisThread;
    private Socket socket;
    private String fileName;

    RunnableSocketReader(Socket soc, String name){
        // Initialize with socket and fileName
        socket = soc;
        fileName = name;
    }

    @Override 
    public void run() {
        File file=new File("/fileName");
        if (file.exists()){
            System.out.println("Client: Warning! " + fileName + " will be overwritten!");
        }

        try{ 
            FileOutputStream fout= new FileOutputStream(fileName);
			DataInputStream socket_reader = new DataInputStream(
					socket.getInputStream());
            int total_bytes = 0;
            int len;
			byte[] buffer = new byte[1024];
            while(true){
                len = socket_reader.read(buffer);
                if(len < 0){
                    System.out.println("Client: Finished downloading " + fileName);
                    break;
                }
                total_bytes += len;
                fout.write(buffer, 0, len);
            }
            System.out.println("Client: Closing socket (" + total_bytes + "bytes transfered)");
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
