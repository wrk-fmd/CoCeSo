package at.wrk.coceso.dto.container;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContainerDto {

    private Long id;
    private Long concern;
    private Long parent;
    private String name;

    private List<Long> children;
    private List<Long> units;
}
