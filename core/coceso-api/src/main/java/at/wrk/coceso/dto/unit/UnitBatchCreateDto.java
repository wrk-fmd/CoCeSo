package at.wrk.coceso.dto.unit;

import at.wrk.coceso.dto.Lengths;
import at.wrk.coceso.dto.point.PointDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * Form data for the batch unit creation
 */
@Getter
@Setter
@NoArgsConstructor
public class UnitBatchCreateDto {

    @NotNull
    @Length(min = 1, max = Lengths.UNIT_CALL)
    private String call;

    private int from;
    private int to;

    private boolean portable;

    private PointDto home;
}
