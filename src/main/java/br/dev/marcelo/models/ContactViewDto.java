package br.dev.marcelo.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(title = "Contact View", description = "Contact data structure")
public class ContactViewDto extends ContactUpdateDto {

    @Schema(title = "Contact identifier", pattern = "UUID")
    @NotEmpty
    private String id;

}
