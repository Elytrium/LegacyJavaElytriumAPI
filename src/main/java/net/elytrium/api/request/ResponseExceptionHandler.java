package net.elytrium.api.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseExceptionHandler.class);

    Response showCustomMessage(Exception e) {
        if (e instanceof HttpClientErrorException.BadRequest) {
            return Response.genBadRequestResponse(((HttpClientErrorException.BadRequest) e).getStatusText());
        } else if (e instanceof HttpClientErrorException.NotFound) {
            return Response.genBadRequestResponse(((HttpClientErrorException.NotFound) e).getStatusText());
        } else if (e instanceof HttpClientErrorException.TooManyRequests) {
            return Response.getTooManyRequestResponse();
        } else {
            logger.error("Unknown exception caught");
            logger.error(e.toString());

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
            logger.error("Unknown exception caught");
            logger.error(e.toString());

            return new ResponseEntity<>(Response.getServerErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}