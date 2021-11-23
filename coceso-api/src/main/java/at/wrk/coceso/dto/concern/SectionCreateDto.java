package at.wrk.coceso.dto.concern;

import at.wrk.coceso.dto.Lengths;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SectionCreateDto {

    @NotNull
    @Length(min = 1, max = Lengths.SECTION_NAME)
    private String name;

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }
}
