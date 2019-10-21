package at.wrk.coceso.radio.mapper;

import java.util.List;

import at.wrk.coceso.radio.api.dto.ReceivedCallDto;
import at.wrk.coceso.radio.api.dto.SendCallDto;
import at.wrk.coceso.radio.entity.RadioCall;
import at.wrk.coceso.radio.entity.RadioCall.Direction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface RadioCallMapper {

    @Mappings({
            @Mapping(target = "direction", source = "emergency"),
            @Mapping(target = "id", ignore = true)
    })
    RadioCall receivedCallToRadioCall(ReceivedCallDto call);

    @Mappings({
            @Mapping(target = "emergency", source = "direction")
    })
    ReceivedCallDto radioCallToReceivedCall(RadioCall call);

    List<ReceivedCallDto> radioCallToReceivedCall(List<RadioCall> call);

    @Mappings({
            @Mapping(target = "direction", constant = "TX"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "timestamp", ignore = true)
    })
    RadioCall sendCallToRadioCall(SendCallDto call);

    default Direction emergencyToEnum(boolean emergency) {
        return emergency ? Direction.RX_EMG : Direction.RX;
    }

    default boolean enumToEmergency(Direction direction) {
        switch (direction) {
            case RX:
                return false;
            case RX_EMG:
                return true;
            default:
                throw new IllegalArgumentException("Not a received call");
        }
    }
}
