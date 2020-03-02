package nl.sogyo.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetRequest implements Request{
    private HttpMethod HTTPmethod;
    private String resourcePath;
    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> parameters = new HashMap<>();

    GetRequest(String resourcePath, BufferedReader reader) {
        this.HTTPmethod = HttpMethod.GET;
        String[] parsedResourcePath = resourcePath.split("\\?");
        this.resourcePath = parsedResourcePath[0];
        if(parsedResourcePath.length>1){
            String[] resources = parsedResourcePath[1].split("&");
            for(String resource:resources){
                String[] parsedResource = resource.split("=");
                this.parameters.put(parsedResource[0].replace("+"," "),parsedResource[1].replace("+"," "));
            }
        }
        try {
            String line = reader.readLine();
            String[] parsedLine = line.split(": ");
            while (parsedLine.length > 1) {
                this.headers.put(parsedLine[0], parsedLine[1]);
                line = reader.readLine();
                parsedLine = line.split(": ");
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
        for(String headerName:this.headers.keySet()){
            headerParameterNames.add(headerName);
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
}
