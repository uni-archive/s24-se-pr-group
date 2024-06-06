package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.HallplanCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.HallPlanCreateRequestMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.HallPlanServiceImpl;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping("/api/v1/hallplan")
public class HallPlanEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final HallPlanServiceImpl hallplanService;
    private final HallPlanCreateRequestMapper hallPlanCreateRequestMapper;

    public HallPlanEndpoint(HallPlanServiceImpl ticketService, HallPlanCreateRequestMapper hallPlanCreateRequestMapper) {
        this.hallplanService = ticketService;
        this.hallPlanCreateRequestMapper = hallPlanCreateRequestMapper;
    }


    @Secured(Authority.Code.ADMIN)
    @PostMapping(path = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(@RequestBody HallplanCreateRequest toCreate) throws ValidationException, ForbiddenException {
        LOGGER.info("Creating hallplan {} {}", toCreate.getSectors().get(0).getStandingOnly(), toCreate.getSectors().get(0).getSpots());
        var retVal = hallplanService.create(hallPlanCreateRequestMapper.toDto(toCreate));
        return ResponseEntity.status(201)
            .body(retVal);
    }
}
