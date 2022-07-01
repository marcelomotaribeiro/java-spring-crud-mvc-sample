package br.dev.marcelo.interceptors;

import br.dev.marcelo.models.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RequestExceptionInterceptor {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> exceptionHandle(final Exception ex) {
        return ResponseEntity.internalServerError().body(new ResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto> httpMessageNotReadableExceptionHandle(
            final HttpMessageNotReadableException ex) {
        final int lineInitPos = ex.getCause().getMessage().indexOf("line: ");
        final int lineEndPos = ex.getCause().getMessage().indexOf(", column: ");
        final int columnInitPos = ex.getCause().getMessage().indexOf("column: ");
        final int columnEndPos = ex.getCause().getMessage().indexOf("] (through reference chain:");
        final var lineError = ex.getCause().getMessage().substring(lineInitPos, lineEndPos);
        final var columnError = ex.getCause().getMessage().substring(columnInitPos, columnEndPos);
        return ResponseEntity.badRequest().body(new ResponseDto(
                                lineError.concat("; ").concat(columnError).concat(".")));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> methodArgumentNotValidExceptionHandle(
            final MethodArgumentNotValidException ex) {
        final List<String> validationFound = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationFound.add(fieldName + ": " + errorMessage);
        });
        return ResponseEntity.badRequest().body(new ResponseDto(
                                String.join("; ", validationFound).concat(".")));
    }

}
