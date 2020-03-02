package nl.sogyo.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestWithContent implements Request{
    private HttpMethod HTTPmethod;
    private String resourcePath;
    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> parameters = new HashMap<>();
    private String content;

    RequestWithContent(String resourcePath, BufferedReader reader) {
        this.HTTPmethod = HttpMethod.POST;
        String[] parsedResourcePath = resourcePath.split("\\?");
        this.resourcePath = parsedResourcePath[0];
        if(parsedResourcePath.length>1){
            addParameters(parsedResourcePath[1]);
        }
        try {
            String line = reader.readLine();
            String[] parsedLine = line.split(": ");
            while (parsedLine.length > 1) {
                this.headers.put(parsedLine[0], parsedLine[1]);
                line = reader.readLine();
                parsedLine = line.split(": ");
            }
            StringBuilder newString = new StringBuilder();
            int contentLength = Integer.parseInt(this.headers.get("Content-Length"));
            while(newString.length() < contentLength){
                newString.append((char)reader.read());
            }
            this.content = newString.toString();
            if(this.headers.get("Content-Type").equals("application/x-www-form-urlencoded")){
                addParameters(this.content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /// Defines the HTTP method that was used by the
    /// client in the incoming request.
    public HttpMethod getHTTPMethod(){
        return HTTPmethod;
    }

    /// Defines the resource path that was requested by
    /// the client. The resource path excludes url parameters.
    public String getResourcePath(){
        return resourcePath;
    }

    /// Defines the names of the header parameters that
    /// were supplied in the request.
    public List<String> getHeaderParameterNames(){
        ArrayList<String> headerParameterNames = new ArrayList<>();
        for(String parameterName:this.headers.keySet()){
            headerParameterNames.add(parameterName);
        }
        return headerParameterNames;
    }

    /// Retrieves the supplied header parameter value
    /// corresponding to the name. If no header exists
    /// with the name, it returns null.
    public String getHeaderParameterValue(String name){
        return this.headers.getOrDefault(name, null);
    }

    /// Retrieves the URL parameters that were present in
    /// the requested URL.
    public List<String> getParameterNames(){
        ArrayList<String> parameterNames = new ArrayList<>();
        for(String parameterName:this.parameters.keySet()){
            parameterNames.add(parameterName);
        }
        return parameterNames;
    }

    /// Retrieves the URL parameter value corresponding to
    /// the name. If no parameter exists with the name,
    /// it returns null.
    public String getParameterValue(String name){
        return this.parameters.getOrDefault(name, null);
    }
    public String getContent(){
        return this.content;
    }

    void addParameters(String line){
        String[] resources = line.split("&");
        for(String resource:resources){
            String[] parsedResource = resource.split("=");
            this.parameters.put(parsedResource[0].replace("+"," "),parsedResource[1].replace("+"," "));
        }

    }
}
