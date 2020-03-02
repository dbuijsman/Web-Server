package nl.sogyo.webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class ResponseException implements Response{
    private HttpStatusCode status;
    private Map<String, String> customHeaders = new HashMap<>();
    private ZonedDateTime date;
    private String content = null;
    ResponseException(HttpStatusCode status){
        this.status = status;
        this.date = ZonedDateTime.now(ZoneId.systemDefault());
        this.customHeaders.put("Content-Type", "text/; charset=UTF-8");
        this.customHeaders.put("Content-Length", "0");
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
            writer.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    /// HTTP status code that informs the client of the
    /// processing of the request by the server.
    public HttpStatusCode getStatus(){
        return status;
    }
    /// The header parameters and values that are unique for
    /// this response. A response defines a set of headers, some
    /// are unique, others are always present. The date header
    /// is always present and, if the response has content,
    /// so are the Content-Type and Content-Length.
    public Map<String, String> getCustomHeaders(){
        return customHeaders;
    }

    /// The exact date and time at which this response was
    /// generated. This is used for the date header that is
    /// always added.
    public ZonedDateTime getDate(){
        return date;
    }

    /// Optionally, a response contains content. If we want
    /// to transfer for example a web page, we add the HTML contents
    /// in the body of the response.
    public String getContent(){
        return content;
    }
}

