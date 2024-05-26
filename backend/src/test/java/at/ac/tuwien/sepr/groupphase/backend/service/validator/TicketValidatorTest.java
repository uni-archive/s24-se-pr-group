package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.dto.TicketDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class TicketValidatorTest {

    @Autowired
    private TicketValidator ticketValidator;

    @Test
    void aNonReservedTicket_ShouldNotBeCancellable() {
        var t = new TicketDetailsDto();
        t.setReserved(false);
        t.setValid(true);

        assertThatThrownBy(() -> ticketValidator.validateForCancelReservation(t))
            .hasMessageContaining("Cannot cancel non-reserved ticket.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void aReservedTicket_ThatIsInvalid_ShouldNotBeCancellable() {
        var t = new TicketDetailsDto();
        t.setReserved(true);
        t.setValid(false);

        assertThatThrownBy(() -> ticketValidator.validateForCancelReservation(t))
            .hasMessageContaining("Cannot cancel invalid ticket.")
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void ticketsShouldFirstBeCheckedForValidity() {
        var t = new TicketDetailsDto();
        t.setReserved(false);
        t.setValid(false);

        assertThatThrownBy(() -> ticketValidator.validateForCancelReservation(t))
            .hasMessageContaining("Cannot cancel invalid ticket.")
            .isInstanceOf(ValidationException.class);
    }
}
