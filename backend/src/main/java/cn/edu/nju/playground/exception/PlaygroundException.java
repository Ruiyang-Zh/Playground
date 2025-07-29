package cn.edu.nju.playground.exception;


import lombok.Getter;

@Getter
public class PlaygroundException extends RuntimeException {
    private final int status;
    private final int code;

    public PlaygroundException(int status, int code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    // Global Exceptions
    public static PlaygroundException badRequest() {
        return new PlaygroundException(400, 101, "Bad Request");
    }

    public static PlaygroundException badRequest(String message) {
        return new PlaygroundException(400, 101, message);
    }

    public static PlaygroundException unexpectedError() {
        return new PlaygroundException(500, 102, "Unexpected Error");
    }

    public static PlaygroundException unexpectedError(String message) {
        return new PlaygroundException(500, 102, message);
    }

    public static PlaygroundException operationFailed() {
        return new PlaygroundException(500, 103, "Operation Failed");
    }

    public static PlaygroundException operationFailed(String message) {
        return new PlaygroundException(500, 103, message);
    }

    public static PlaygroundException operationNotAllowed() {
        return new PlaygroundException(403, 104, "Operation Not Allowed");
    }

    public static PlaygroundException operationNotAllowed(String message) {
        return new PlaygroundException(403, 104, message);
    }

    public static PlaygroundException notLoggedIn() {
        return new PlaygroundException(401, 105, "Not Logged In");
    }

    public static PlaygroundException notLoggedIn(String message) {
        return new PlaygroundException(401, 105, message);
    }

    // User Exceptions
    public static PlaygroundException userNotFound() {
        return new PlaygroundException(404, 201, "User Not Found");
    }

    public static PlaygroundException userNotFound(String message) {
        return new PlaygroundException(404, 201, message);
    }

    public static PlaygroundException userAlreadyExists() {
        return new PlaygroundException(409, 202, "User Already Exists");
    }

    public static PlaygroundException userAlreadyExists(String message) {
        return new PlaygroundException(409, 202, message);
    }

    public static PlaygroundException invalidCredentials() {
        return new PlaygroundException(401, 203, "Invalid Credentials");
    }

    public static PlaygroundException invalidCredentials(String message) {
        return new PlaygroundException(401, 203, message);
    }



}
