package nl.sogyo.webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResponseGetResource implements Response{
    private HttpStatusCode status;
    private Map<String, String> customHeaders = new HashMap<>();
    private ZonedDateTime date;
    private byte[] content;
    ResponseGetResource(String resourcePath){
        this.date = ZonedDateTime.now();
        File requestedFile = new File("src/main/resources" + resourcePath);
        try {
            if(requestedFile.exists()) {
                this.content = Files.readAllBytes(requestedFile.toPath());
                this.status = HttpStatusCode.OK;
            } else {
                this.status = HttpStatusCode.NotFound;
            }
        } catch (IOException e) {
            this.status = HttpStatusCode.ServerError;
        } finally {
            String type = resourcePath.substring(resourcePath.indexOf(".") + 1);
            this.customHeaders.put("Content-Type", "text/" + type + "; charset=UTF-8");
            if(this.content != null) {
                this.customHeaders.put("Content-Length", String.valueOf(this.content.length));
            } else{
                this.customHeaders.put("Content-Length", "0");
            }
        }
    }

    public void sendTo(Socket socket){
        try {
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            writer.write(("HTTP/1.1 " + this.status.getCode() + " " + this.status.getDescription() + "\r\n").getBytes());
            writer.write(("Date: " + this.date + "\r\n").getBytes());
            writer.write(("Server: MyOwnServer" + "\r\n").getBytes());
            writer.write(("Connection: keep-alive" + "\r\n").getBytes());
            for(String customHeader:this.customHeaders.keySet()){
                writer.write((customHeader + ": " + this.customHeaders.get(customHeader) + "\r\n").getBytes());
            }
            writer.write(("\r\n".getBytes()));
            if(this.content != null) {
                writer.write(this.content);
            }
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


