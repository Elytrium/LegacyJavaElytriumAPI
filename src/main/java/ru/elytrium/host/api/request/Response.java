package ru.elytrium.host.api.request;

import ru.elytrium.host.api.model.Exclude;

public class Response {

    private boolean success;

    private String message;

    private String answer;

    @Exclude
    private int code;

    public Response(boolean success, String message, String answer, int code) {
        this.success = success;
        this.message = message;
        this.answer = answer;
        this.code = code;
    }

    public static Response genSuccessResponse() {
        return new Response(true, "OK", null, 200);
    }

    public static Response genSuccessResponse(String answer) {
        return new Response(true, "OK", answer, 200);
    }

    public static Response genBadRequestResponse(String message) {
        return new Response(false, message, null, 400);
    }

    public static Response genForbiddenResponse(String message) {
        return new Response(false, message, null, 403);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getAnswer() {
        return answer;
    }

    public int getCode() {
        return code;
    }
}
