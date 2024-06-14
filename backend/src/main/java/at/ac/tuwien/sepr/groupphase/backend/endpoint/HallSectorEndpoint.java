package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.dto.HallSectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowResponse;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.HallSectorServiceImpl;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hallsector")
public class HallSectorEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private HallSectorServiceImpl hallSectorService;

    public HallSectorEndpoint(HallSectorServiceImpl hallSectorService) {
        this.hallSectorService = hallSectorService;
    }

    @PermitAll
    @GetMapping("/{hallplanId}")
    public ResponseEntity<List<HallSectorDto>> getShowByLocation(@PathVariable("hallplanId") Long hallPlanId) {
        return ResponseEntity.ok(
            hallSectorService.getByHallPlanId(hallPlanId));
    }
}
