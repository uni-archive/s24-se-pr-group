package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.dto.SectorTicketAddToOrderDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketAddToOrderDto;
import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorTicketCreationRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketCreationRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDetailsResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {HallSpotResponseMapper.class})
public interface TicketResponseMapper extends BaseResponseMapper<TicketDetailsDto, TicketDetailsResponse> {
    TicketAddToOrderDto toDto(TicketCreationRequest response);

    SectorTicketAddToOrderDto toSectorDto(SectorTicketCreationRequest response);

    @Override
    @Mapping(target = "order.tickets", ignore = true)
    TicketDetailsResponse toResponse(TicketDetailsDto dto);
}
