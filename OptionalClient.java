import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class OptionalClient{
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		System.out.println("Hello World");
        String str;
        Pattern responseRegex = Pattern.compile("(\\d{3}) (.+)");
        Pattern inputRegex = Pattern.compile("(USER|SYST|EPSV|RETR|STOR|LIST|PASS)( (.*))?");
        Pattern portRegex = Pattern.compile("\\|(\\d+)\\|");

        Socket socket = new Socket("localhost",9876);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader socket_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
        List<RunnableSocketReader> readerThreads = new ArrayList<RunnableSocketReader>();

        int port = -1;
        boolean passiveMode = false;
		while(true){
            String response = checkResponse(socket_reader, 220);
            if(response == ""){
                return;
            }
            while(true){
                // get command from user
                str = reader.readLine();
                Matcher umatch = inputRegex.matcher(str);

                if(umatch.find()){
                    String cmd = umatch.group(1);
                    String arg = umatch.group(3);
                    System.out.println("Client: cmd was " + cmd +  " Arg was: " + arg);

                    if(cmd.compareTo("USER") == 0){
                        writer.writeBytes(str + "\r\n");
                        response = checkResponse(socket_reader, 230);
                        if(response.isEmpty()){
                            continue;
                        }
                    }

                    if(cmd.compareTo("SYST") == 0){
                        writer.writeBytes(str + "\r\n");
                        response = checkResponse(socket_reader, 215);
                        if(response.isEmpty()){
                            continue;
                        }else{
                            System.out.println("Client: Server has: " + response);
                        }
                    }

                    if(cmd.compareTo("EPSV") == 0){
                        writer.writeBytes(str + "\r\n");
                        response = checkResponse(socket_reader, 229);
                        Matcher rmatch = portRegex.matcher(response);
                        if(rmatch.find()){
                            port = Integer.parseInt(rmatch.group(1));
                            passiveMode = true;
                            System.out.println("Client: Entering passive mode, port=" + port);
                        }else{
                            System.out.println("Client: Server returned an invalid msg: " + response);
                            continue;
                        }
                    }

                    if(cmd.compareTo("RETR") == 0){
                        System.out.println("Client: In RETR handler"); 
                        if (!passiveMode || port == -1){
                            System.out.println("Client: Not in passive mode / port not set!");
                            continue;
                        }
                        writer.writeBytes(str + "\r\n");
                        RunnableSocketReader sock_reader = new RunnableSocketReader(port, arg);
                        sock_reader.start();

                        response = checkResponse(socket_reader, 150);
                        if(response.isEmpty()){
                            response = checkResponse(socket_reader, 426);
                            if (response.isEmpty()){
                                System.out.println("Client: Error when 'Could not download file' WTF??");
                            }else{
                                System.out.println("Client: Could not downlaod file!");
                            }
                            continue;
                        }else{
                            // start reading from the socket as soon as a connection is accepted
                            System.out.println("Client: Downloading files...");
                        }
                        response = checkResponse(socket_reader, 226);
                    }

                }else{
                    System.out.println("Client: Invalid command: " + str);
                }
            }

            //int socketNumber = Integer.parseInt(response);
            //System.out.println("Client: Server opened port " + socketNumber);

            // start reading from the socket as soon as a connection is accepted
            //RunnableSocketReader sock_reader = new RunnableSocketReader(socketNumber, str);
            //sock_reader.start();

            // add to readerTheads to wait() later
            //readerThreads.add(sock_reader);
            //socket.close();
		}
	}

    static String checkResponse(BufferedReader sockReader, int expected) throws Exception{
        Pattern responseRegex = Pattern.compile("(\\d{3}) (.+)");
        String response = sockReader.readLine();
        Matcher match = responseRegex.matcher(response);
        if (match.find()){
            int found = Integer.parseInt(match.group(1));
            if(found == expected){
                return match.group(2);
            }else{
                System.out.println("Client: Server responded with: " + found + " expected: " + expected);
                return "";
            }
        }else{
            System.out.println("Client: Server failed to return a valid message: " + response);
            return "";
        }
    }
}

