package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.HallplanCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.HallPlanCreateRequestMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ForbiddenException;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.HallPlanServiceImpl;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @PermitAll
    @PostMapping(path = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(@RequestBody HallplanCreateRequest toCreate) throws ValidationException, ForbiddenException {
        var retVal = hallplanService.create(hallPlanCreateRequestMapper.toDto(toCreate));
        return ResponseEntity.status(201)
            .body(retVal);
    }
}
