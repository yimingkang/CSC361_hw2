import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class OptionalClient{
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		System.out.println("Hello World");
        String str;
        Pattern inputRegex = Pattern.compile("(USER|SYST|EPSV|RETR|STOR|LIST|PASS)( (.*))?");
        Pattern portRegex = Pattern.compile("\\|(\\d+)\\|");

        String host = "loki.cciw.ca";
        Socket socket = new Socket(host, 21);

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
                            continue;
                        }
                    }

                    if(cmd.compareTo("STOR") == 0){
                        if (!passiveMode || port == -1){
                            System.out.println("Client: Not in passive mode / port not set!");
                            continue;
                        }

                        File file = new File(arg);
                        if (!file.exists()){
                            System.out.println("Client: File not found: " + arg);
                            port = -1;
                            passiveMode = false;
                            continue;
                        }
                        System.out.println("Client: About to store file: " + str);

                        // start connection
                        RunnableFileWriter fileWriter = new RunnableFileWriter(port, arg, host);
                        fileWriter.start();
                        writer.writeBytes(str + "\r\n");

                        response = checkResponse(socket_reader, 150);
                        if(response.isEmpty()){
                            port = -1;
                            passiveMode = false;
                            continue;
                        }
                        response = checkResponse(socket_reader, 226);
                        port = -1;
                        passiveMode = false;
                    }

                    if(cmd.compareTo("RETR") == 0){
                        if (!passiveMode || port == -1){
                            System.out.println("Client: Not in passive mode / port not set!");
                            continue;
                        }
                        writer.writeBytes(str + "\r\n");
                        RunnableSocketReader sock_reader = new RunnableSocketReader(port, arg, host);
                        sock_reader.start();

                        response = checkResponse(socket_reader, 150);
                        if(response.isEmpty()){
                            response = checkResponse(socket_reader, 426);
                            if (response.isEmpty()){
                                System.out.println("Client: Error when 'Could not download file' WTF??");
                            }else{
                                System.out.println("Client: Could not downlaod file!");
                            }
                            port = -1;
                            passiveMode = false;
                            continue;
                        }else{
                            // start reading from the socket as soon as a connection is accepted
                            System.out.println("Client: Downloading files...");
                        }
                        response = checkResponse(socket_reader, 226);
                        port = -1;
                        passiveMode = false;
                    }

                    if(cmd.compareTo("LIST") == 0){
                        if (!passiveMode || port == -1){
                            System.out.println("Client: Not in passive mode / port not set!");
                            continue;
                        }
                        writer.writeBytes(str + "\r\n");

                        Socket dataSoc= new Socket("localhost", port);
                        BufferedReader dataSocReader = new BufferedReader(new InputStreamReader(dataSoc.getInputStream()));

                        response = checkResponse(socket_reader, 150);
                        if(response.isEmpty()){
                            System.out.println("Client: Unexpected server response");
                            port = -1;
                            passiveMode = false;
                            continue;
                        }

                        String buf = dataSocReader.readLine();

                        System.out.println("LIST on " + arg + ":");
                        while(buf != null){
                            System.out.println(buf);
                            buf = dataSocReader.readLine();
                        }
                        dataSoc.close();
                        port = -1;
                        passiveMode = false;
                        response = checkResponse(socket_reader, 226);
                        System.out.println("Client: Done listing");
                    }

                    if(cmd.compareTo("PASS") == 0){
                        writer.writeBytes(str + "\r\n");
                        response = checkResponse(socket_reader, 226);
                    }

                }else{
                    System.out.println("Client: Invalid command: " + str);
                }
            }

		}
	}

    static String checkResponse(BufferedReader sockReader, int expected) throws Exception{
        Pattern responseRegex = Pattern.compile("(\\d{3}) (.+)");
        while (true){
            String response = sockReader.readLine();
            Matcher match = responseRegex.matcher(response);
            if(!match.find()){
                System.out.println(response);
                continue;
            }
            System.out.println(response);
            int found = Integer.parseInt(match.group(1));
            if(found == expected){
                System.out.println("******DONE********");
                return match.group(2);
            }else{
                System.out.println("Client: Server responded with: " + found + " expected: " + expected);
                System.out.println("******DONE********");
                return "";
            }
        }
    }
}

