package nl.sogyo.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private Socket socket;
    /*private static InetAddress address;

    static {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private int port = 9000;*/

    public ConnectionHandler(Socket toHandle) {
        this.socket = toHandle;
    }

    /// Handle the incoming connection. This method is called by the JVM when passing an
    /// instance of the connection handler class to a Thread.
    public void run() {
        try {
            // Set up a reader that can conveniently read our incoming bytes as lines of text.
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            try {
                String line = reader.readLine();
                String[] parsedLine = line.split(" ");
                Request request;
                Response response;
                if (!parsedLine[1].contains(".")) {
                    throw new Exception();
                }
                switch (parsedLine[0]) {
                    case "GET":
                        request = new GetRequest(parsedLine[1], reader);
                        response = new ResponseGetResource(request.getResourcePath());
                        break;
                    case "POST":
                        request = new RequestWithContent(parsedLine[1], reader);
                        response = new ResponsePost(request.getResourcePath(), ((RequestWithContent) request).getContent());
                        break;
                    case "PUT":
                        request = new RequestWithContent(parsedLine[1], reader);
                        response = new ResponsePut(request.getResourcePath(), ((RequestWithContent) request).getContent());
                        break;
                    case "DELETE":
                        request = new RequestWithContent(parsedLine[1], reader);
                        response = new ResponseDelete(request.getResourcePath());
                        break;
                    default:
                        response = new ResponseException(HttpStatusCode.NotImplemented);
                }
                //if(request.getResourcePath().contains(".")) {
                //response = new ResponseGetResource(request.getResourcePath());
                //} else if(request.getResourcePath().contains("/api/")){
                //String content = makeContent_RequestPathHeadersAndParameters(request);
                //response = new ResponseAPI(address, port, request);
                //} else{
                //String content = makeContent_RequestPathHeadersAndParameters(request);
                //response = new ResponseStatus(content);
                //}
                response.sendTo(socket);
            }catch (Exception e){
                Response response = new ResponseException(HttpStatusCode.BadRequest);
                response.sendTo(socket);
                e.printStackTrace();

            }

        } catch (IOException e) {
            Response response = new ResponseException(HttpStatusCode.ServerError);
            response.sendTo(socket);
            e.printStackTrace();
        } finally {
            // After handling the request, we can close our socket.
            try{
                socket.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    /*String makeContent_RequestAndPath(Request request){
        StringBuilder newString = new StringBuilder("There was an HTTP " + request.getHTTPMethod() + " request");
        if(!request.getResourcePath().equals("/")){
            newString.append(" and you requested the following source: " + request.getResourcePath());
        }
        if (request.getHTTPMethod() == HttpMethod.POST) {
            newString.append(" with body: " + ((RequestWithContent) request).getContent());
        }
        return newString.toString();
    }
    String makeContent_RequestPathHeadersAndParameters(Request request){
        StringBuilder newString = new StringBuilder(makeContent_RequestAndPath(request));
        newString.append("\n \n");
        newString.append("The following header parameters were passed:\n");
        for(String header:request.getHeaderParameterNames()){
            newString.append(header + ": " + request.getHeaderParameterValue(header) + "\r\n");
        }
        if(request.getParameterNames().size()>0) {
            newString.append("\n");
            newString.append("The following parameters were passed:\n");
            for(String parameter:request.getParameterNames()){
                newString.append(parameter + ": " + request.getParameterValue(parameter) + "\r\n");
            }
        }
        return newString.toString();
    }*/
    public static void main(String... args) {
        try {
            // A server socket opens a port on the local computer (in this program port 9090).
            // The computer now listens to connections that are made using the TCP/IP protocol.
            ServerSocket socket = new ServerSocket(9090);
            System.out.println("Application started. Listening at localhost:9090");

            // Start an infinite loop. This pattern is common for applications that run indefinitely
            // and react on system events (e.g. connection established). Inside the loop, we handle
            // the connection with our application logic. 
            while(true) {
                // Wait for someone to connect. This call is blocking; i.e. our program is halted
                // until someone connects to localhost:9090. A socket is a connection (a virtual
                // telephone line) between two endpoints - the client (browser) and the server (this).
                Socket newConnection = socket.accept();
                // We want to process our incoming call. Furthermore, we want to support multiple
                // connections. Therefore, we handle the processing on a background thread. Java
                // threads take a class that implements the Runnable interface as a constructor
                // parameter. Upon starting the thread, the run() method is called by the JVM.
                // As our handling is in a background thread, we can accept new connections on the
                // main thread (in the next iteration of the loop).
                // Starting the thread is so-called fire and forget. The main thread starts a second
                // thread and forgets about its existence. We receive no feedback on whether the
                // connection was handled gracefully.
                Thread t = new Thread(new ConnectionHandler(newConnection));
                t.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public InetAddress getAddress(){return address;}
    public int getPort(){return port;}
    public void setAddress(InetAddress newAddress){
        this.address = newAddress;
    }
    public void setPort(int newPort){
        this.port = newPort;
    }*/
}
