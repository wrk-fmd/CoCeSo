package at.wrk.coceso.dto.journal;

import at.wrk.coceso.dto.Lengths;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CustomJournalEntryDto {

    private Long unit;
    private Long incident;

    @NotNull
    @Length(min = 1, max = Lengths.LOG_TEXT)
    private String text;

    public void setText(String text) {
        this.text = text != null ? text.trim() : null;
    }
}
