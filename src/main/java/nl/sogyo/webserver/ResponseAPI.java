package nl.sogyo.webserver;

        import java.io.*;
        import java.net.*;
        import java.time.ZonedDateTime;
        import java.util.Arrays;
        import java.util.HashMap;
        import java.util.Map;

public class ResponseAPI implements Response{
    private HttpStatusCode status;
    private Map<String, String> customHeaders = new HashMap<>();
    private ZonedDateTime date;
    private byte[] content;
    ResponseAPI(InetAddress address, int port, Request request) {
        this.date = ZonedDateTime.now();
        try {
            Socket socketToAPI = new Socket(address, port);
            BufferedWriter writerAPI = new BufferedWriter(new OutputStreamWriter(socketToAPI.getOutputStream(), "UTF8"));
            writerAPI.write(request.getHTTPMethod() + " " + request.getResourcePath());
            if(!request.getParameterNames().isEmpty() && request.getHTTPMethod()==HttpMethod.GET){
                writerAPI.write("?");
                StringBuilder parameterString = new StringBuilder();
                for(String parameter:request.getParameterNames()){
                    parameterString.append("&").append(parameter).append("=").append(request.getParameterValue(parameter));
                }
                writerAPI.write(parameterString.substring(1));
            }
            writerAPI.write(" HTTP/1.1 \r\n");
            for(String header:request.getHeaderParameterNames()){
                if(!header.equals("Referer") && !header.equals("Host")) {
                    writerAPI.write(header + ": " + request.getHeaderParameterValue(header).replace("9090", "9000") + "\r\n");
                    System.out.print(header + ": " + request.getHeaderParameterValue(header).replace("9090", "9000") + "\r\n");
                }
            }
            if(request.getHTTPMethod()==HttpMethod.POST){
                writerAPI.write("\r\n");
                writerAPI.write(((RequestWithContent) request).getContent());
            }
            writerAPI.flush();
            BufferedReader readerAPI = new BufferedReader(new InputStreamReader(socketToAPI.getInputStream()));
            try {
                String line = readerAPI.readLine();
                System.out.println(line);
                line = readerAPI.readLine();
                System.out.println(line);
                String[] parsedLine = line.split(": ");
                while (parsedLine.length > 1){
                    line = readerAPI.readLine();
                    System.out.println(line);
                    parsedLine = line.split(": ");
                }
                StringBuilder newString = new StringBuilder();
                int contentLength = Integer.parseInt(this.customHeaders.get("Content-Length"));
                while(newString.length() < contentLength){
                    newString.append((char)readerAPI.read());
                }
                this.content = newString.toString().getBytes();
                this.status = HttpStatusCode.OK;
            } catch (IOException e) {
                e.printStackTrace();
                this.status = HttpStatusCode.ServerError;
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //String makeResponseString

    public void sendTo(Socket socket){
        try {
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            writer.write((new String("HTTP/1.1 " + this.status.getCode() + " " + this.status.getDescription() + "\r\n")).getBytes());
            writer.write((new String("Date: " + this.date + "\r\n")).getBytes());
            writer.write((new String("Server: MyOwnServer" + "\r\n")).getBytes());
            writer.write((new String("Connection: keep-alive" + "\r\n")).getBytes());
            for(String customHeader:this.customHeaders.keySet()){
                writer.write((new String(customHeader + ": " + this.customHeaders.get(customHeader) + "\r\n")).getBytes());
            }
            writer.write((new String("\r\n").getBytes()));
            writer.write(this.content);
            writer.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    /// HTTP status code that informs the client of the
    /// processing of the request by the server.
    public HttpStatusCode getStatus(){
        return this.status;
    }
    /// The header parameters and values that are unique for
    /// this response. A response defines a set of headers, some
    /// are unique, others are always present. The date header
    /// is always present and, if the response has content,
    /// so are the Content-Type and Content-Length.
    public Map<String, String> getCustomHeaders(){
        return this.customHeaders;
    }

    /// The exact date and time at which this response was
    /// generated. This is used for the date header that is
    /// always added.
    public ZonedDateTime getDate(){
        return this.date;
    }

    /// Optionally, a response contains content. If we want
    /// to transfer for example a web page, we add the HTML contents
    /// in the body of the response.
    public String getContent(){
        return Arrays.toString(this.content);
    }
}


