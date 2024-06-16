package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSeatDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.HallplanSeatCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.ShowHallplanSpotResponse;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.HallSeat;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ShowHallSpotResponseMapper extends BaseResponseMapper<HallSeatDto, ShowHallplanSpotResponse> {

    @Mapping(target = "isReserved", expression = "java(ticketDetailsForSpot != null)")
    ShowHallplanSpotResponse toResponse(HallSeatDto dto, @Context TicketDetailsDto ticketDetailsForSpot);

}