package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.service.job.UnlockUserJob;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

@Service
public class UserUnlockSchedulingService {

    private final SchedulerFactoryBean schedulerFactoryBean;

    public UserUnlockSchedulingService(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    public void scheduleUnlockUser(String username) throws SchedulerException {
        Optional<JobKey> userJobs = schedulerFactoryBean.getScheduler()
            .getJobKeys(GroupMatcher.jobGroupEquals("userJobs")).stream()
            .filter(jobKey -> jobKey.getName().equals("unlockUser-" + username))
            .findFirst();

        JobDetail jobDetail = JobBuilder.newJob(UnlockUserJob.class)
            .withIdentity("unlockUser-" + username, "userJobs")
            .usingJobData("username", username)
            .storeDurably()
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("unlockUserTrigger-" + username, "userTriggers")
            .startAt(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
            .forJob(jobDetail)
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
            .build();

        if (userJobs.isPresent()) {
            schedulerFactoryBean.getScheduler().rescheduleJob(trigger.getKey(), trigger);
        } else {
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
        }
    }
}
