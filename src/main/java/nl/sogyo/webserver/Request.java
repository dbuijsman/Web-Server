package nl.sogyo.webserver;

import java.util.List;
import java.util.Optional;

/// Representation of the incoming HTTP request.
public interface Request {

    /// Defines the HTTP method that was used by the
    /// client in the incoming request.
    HttpMethod getHTTPMethod();

    /// Defines the resource path that was requested by
    /// the client. The resource path excludes url parameters.
    String getResourcePath();

    /// Defines the names of the header parameters that
    /// were supplied in the request.
    List<String> getHeaderParameterNames();

    /// Retrieves the supplied header parameter value
    /// corresponding to the name. If no header exists
    /// with the name, it returns null.
    String getHeaderParameterValue(String name);

    /// Retrieves the URL parameters that were present in
    /// the requested URL.
    List<String> getParameterNames();

    /// Retrieves the URL parameter value corresponding to
    /// the name. If no parameter exists with the name,
    /// it returns null.
    String getParameterValue(String name);
}
