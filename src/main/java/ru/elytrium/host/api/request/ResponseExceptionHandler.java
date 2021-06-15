package ru.elytrium.host.api.request;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.elytrium.host.api.ElytraHostAPI;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    Response showCustomMessage(Exception e) {
        if (e instanceof HttpClientErrorException.BadRequest) {
            return Response.genBadRequestResponse(((HttpClientErrorException.BadRequest) e).getStatusText());
        } else if (e instanceof HttpClientErrorException.NotFound) {
            return Response.genBadRequestResponse(((HttpClientErrorException.NotFound) e).getStatusText());
        } else if (e instanceof HttpClientErrorException.TooManyRequests) {
            return Response.getTooManyRequestResponse();
        } else {
            ElytraHostAPI.getLogger().fatal("Unknown exception caught");
            ElytraHostAPI.getLogger().fatal(e);

            return Response.getServerErrorResponse();
        }
    }

    @ExceptionHandler(Throwable.class)
    public @ResponseBody ResponseEntity<Response> handleDefaultException(Throwable e) {
        if (e instanceof HttpClientErrorException.BadRequest) {
            return new ResponseEntity<>(
                    Response.genBadRequestResponse(((HttpClientErrorException.BadRequest) e).getMessage()),
                    HttpStatus.BAD_REQUEST);
        } else if (e instanceof NoHandlerFoundException) {
            return new ResponseEntity<>(
                    Response.genBadRequestResponse(e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } else if (e instanceof HttpClientErrorException.TooManyRequests) {
            return new ResponseEntity<>(
                    Response.getTooManyRequestResponse(),
                    HttpStatus.BAD_REQUEST);
        } else {
            ElytraHostAPI.getLogger().fatal("Unknown exception caught");
            ElytraHostAPI.getLogger().fatal(e);

            return new ResponseEntity<>(Response.getServerErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}