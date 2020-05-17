package at.wrk.coceso.dto.concern;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class ConcernDto {

    private Long id;
    private String name;
    private String info;
    private boolean closed;
    private Collection<String> sections;
}
