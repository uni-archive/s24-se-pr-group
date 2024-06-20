package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallPlanDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.hallplan.HallplanCreateRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.HallPlanCreateRequestMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.DtoNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.HallPlanServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<?> register(@RequestBody HallplanCreateRequest toCreate) {
        LOGGER.trace("Creating hallplan with name {}", toCreate.getName());
        HallPlanDto retVal = null;
        try {
            retVal = hallplanService.createHallPlan(hallPlanCreateRequestMapper.toDto(toCreate));
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        return ResponseEntity.status(201)
            .body(retVal);
    }

    @Secured(Authority.Code.ADMIN)
    @PostMapping(path = "/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> findByName(@PathVariable("name") String name) {
        var retVal = hallplanService.findByName(name);
        return ResponseEntity.status(200).body(retVal);
    }

    @Secured(Authority.Code.USER)
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HallPlanDto> getById(@RequestBody Long id) {
        HallPlanDto retVal = null;
        try {
            retVal = hallplanService.findById(id);
        } catch (DtoNotFoundException e) {
            throw new NotFoundException(e);
        }
        return ResponseEntity.status(200).body(retVal);
    }
}
