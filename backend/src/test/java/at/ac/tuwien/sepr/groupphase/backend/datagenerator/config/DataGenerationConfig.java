package at.ac.tuwien.sepr.groupphase.backend.datagenerator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataGenerationConfig {

    @Value("${data.artist}")
    public Long artistAmount;

    @Value("${data.event}")
    public Long eventAmount;

    @Value("${data.location}")
    public Long locationAmount;

    @Value("${data.show}")
    public Long showAmount;

    @Value("${data.ticket}")
    public Long ticketAmount;

    @Value("${data.user}")
    public Long userAmount;

    @Value("${data.order}")
    public Long orderAmount;

    @Value("${data.hallPlan}")
    public Long hallPlanAmount;

    @Value("${data.news}")
    public Long newsAmount;
}
