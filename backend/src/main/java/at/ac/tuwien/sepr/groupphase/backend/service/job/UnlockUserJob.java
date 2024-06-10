package at.ac.tuwien.sepr.groupphase.backend.service.job;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class UnlockUserJob extends QuartzJobBean {


    private static final Logger log = LoggerFactory.getLogger(UnlockUserJob.class);
    private final UserDao userDao;

    public UnlockUserJob(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            ApplicationUserDto byEmail = userDao.findByEmail(context.getMergedJobDataMap().getString("username"));
            byEmail.setAccountLocked(false);
            userDao.update(byEmail);
        } catch (EntityNotFoundException e) {
             log.warn("User with username {} not found. Could not unlock user.", context.getMergedJobDataMap().getString("username"));
        }
    }
}
