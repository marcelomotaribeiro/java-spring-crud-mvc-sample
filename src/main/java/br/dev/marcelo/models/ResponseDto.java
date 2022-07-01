package br.dev.marcelo.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(title = "Response", description = "Response data")
public class ResponseDto {

    private String message;

}
