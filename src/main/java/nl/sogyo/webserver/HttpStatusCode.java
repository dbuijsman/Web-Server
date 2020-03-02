package nl.sogyo.webserver;

/// Predefined status codes that inform the client about the handing of the request.
public enum HttpStatusCode {
    /// Processing of the request completed succesfully.
    OK(200, "OK"),
    /// New file is created.
    Created(201,"Created"),
    /// Request was handled
    NoContent(204,"No Content"),
    /// HttpMethod of request was not implemented
    BadRequest(400, "Bad Request"),
    /// Requested resource path was not found.
    NotFound(404, "Not Found"),
    /// An unexpected error occured while handling the request.
    ServerError(500, "Server Error"),
    /// Request is not implemented
    NotImplemented(501, "Not Implemented");

    private int code;
    private String description;

    private HttpStatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /// The numeric status code that is used by the client
    /// to determine error handling.
    public int getCode() {
        return code;
    }

    /// A short textual representation of the status code.
    /// Generally not used by applications, but gives the number 
    /// a human-readable description.
    public String getDescription() {
        return description;
    }
}
