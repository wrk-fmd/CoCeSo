package at.wrk.coceso.dto.container;

import at.wrk.coceso.dto.Lengths;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ContainerCreateDto {

    @NotNull
    @Length(min = 1, max = Lengths.CONTAINER_NAME)
    private String name;

    private Integer index;
    private Long parent;

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }
}
