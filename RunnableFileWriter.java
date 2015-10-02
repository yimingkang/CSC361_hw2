import java.net.*;
import java.io.*;

class RunnableFileWriter implements Runnable {
    private Thread thisThread;
    private int socketNumber;
    private String fileName;

    RunnableFileWriter(int sNumber, String name){
        // Initialize with socket and fileName
        fileName = name;
        socketNumber = sNumber;
    }

    @Override 
    public void run() {
        File file=new File(fileName);

        try{ 
			Socket socket = new Socket("localhost", socketNumber);

            DataOutputStream writer = new DataOutputStream(socket.getOutputStream()); 
            System.out.println("Writer: Established connection for file " + fileName);

            // Send the file here
            byte[] buffer = new byte[1024];
            FileInputStream fin= new FileInputStream(file);
            System.out.println("Writer: Begin transmitting file");
            int total_bytes = 0;
            long start = System.currentTimeMillis();
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
            long diff = System.currentTimeMillis() - start;
            System.out.println("Writer: Done transmitting file (" + total_bytes + " bytes, took " + diff +"ms)");
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
