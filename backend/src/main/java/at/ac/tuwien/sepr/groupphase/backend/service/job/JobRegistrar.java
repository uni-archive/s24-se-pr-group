package at.ac.tuwien.sepr.groupphase.backend.service.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@Configuration
public class JobRegistrar {

    @Bean
    public JobDetailFactoryBean reservationInvalidationJobDetailFactory() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(InvalidateReservationJob.class);
        jobDetailFactory.setDescription("Invoke Invalidation service...");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }
}
