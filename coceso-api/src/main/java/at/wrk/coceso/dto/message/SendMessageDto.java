package at.wrk.coceso.dto.message;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SendMessageDto {

    @NotNull
    @Length(min = 1)
    private String message;
}
