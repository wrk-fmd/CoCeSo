package at.wrk.coceso.dto.concern;

import at.wrk.coceso.dto.Lengths;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class ConcernUpdateDto {

    @Length(min = 1, max = Lengths.CONCERN_NAME)
    private String name;

    @Length(max = Lengths.CONCERN_INFO)
    private String info;

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public void setInfo(String info) {
        this.info = info != null ? info.trim() : null;
    }
}
