package br.dev.marcelo.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Schema(title = "Contact Update", description = "Contact data structure for update")
public class ContactUpdateDto {

    @Schema(title = "Contact name")
    @NotEmpty
    private String name;

    @Schema(title = "Contact mail", format = "Email")
    @NotEmpty
    private List<String> emails;

}
