package br.dev.marcelo.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Schema(title = "Contact", description = "Contact data")
public class ContactDto {

    @Schema(title = "Contact identifier",
            required = true,
            pattern = "UUID",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @Schema(title = "Contact name",
            required = true)
    @NotEmpty
    private String name;

    @Schema(title = "Contact mail",
            required = true,
            format = "Email")
    @NotEmpty
    private List<String> emails;

}
