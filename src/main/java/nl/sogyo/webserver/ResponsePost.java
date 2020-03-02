package nl.sogyo.webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResponsePost implements Response{
    private HttpStatusCode status;
    private Map<String, String> customHeaders = new HashMap<>();
    private ZonedDateTime date;
    private byte[] content;
    ResponsePost(String resourcePath, String content){
        this.date = ZonedDateTime.now();
        String createdPath = createFile(resourcePath, content);
        String type = createdPath.substring(resourcePath.indexOf(".") + 1);
        this.content = ("Made new resource: " + createdPath).getBytes();
        this.customHeaders.put("Content-Type", "text/" + type + "; charset=UTF-8");
        this.customHeaders.put("Content-Length", String.valueOf(this.content.length));
    }


    private String createFile(String resourcePath, String content) {
        Charset utf8 = StandardCharsets.UTF_8;
        StringBuilder newPath = new StringBuilder(resourcePath);
        newPath.insert(newPath.indexOf("."), "[-]");
        int counter = 0;
        File requestedFile = new File("src/main/resources" + resourcePath);
        while(requestedFile.exists()){
            newPath.replace(newPath.indexOf("[")+1,newPath.indexOf("[")+2, String.valueOf(counter));
            counter++;
            requestedFile = new File("src/main/resources" + newPath.toString());
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(requestedFile.getPath()), utf8))) {
            writer.write(content);
            this.status = HttpStatusCode.Created;
            return requestedFile.getPath().substring(18);
        } catch (IOException e) {
            this.status = HttpStatusCode.ServerError;
            return null;
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