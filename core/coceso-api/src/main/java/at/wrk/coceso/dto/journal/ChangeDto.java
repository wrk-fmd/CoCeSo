package at.wrk.coceso.dto.journal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangeDto {

    private String key;
    private Object oldValue;
    private Object newValue;
}
