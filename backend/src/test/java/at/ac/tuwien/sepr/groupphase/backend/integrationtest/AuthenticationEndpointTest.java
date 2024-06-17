package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.UserDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.UserType;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.AddressRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.InvoiceRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.persistence.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
public class AuthenticationEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;


    @BeforeEach
    void setUp() {
        ApplicationUser byEmail = userRepository.findByEmail("testemail@email.com");
        if (Objects.nonNull(byEmail)) {
            userRepository.delete(byEmail);
        }
        ApplicationUser applicationuser = new ApplicationUser();
        applicationuser.setEmail("testemail@email.com");
        applicationuser.setPassword(passwordEncoder.encode("password"));
        applicationuser.setSalt("");
        applicationuser.setType(UserType.CUSTOMER);
        applicationuser.setFirstName("Test");
        applicationuser.setFamilyName("Test");
        applicationuser.setAccountLocked(false);
        applicationuser.setAccountActivated(true);
        userRepository.save(applicationuser);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void authenticateShouldFailForInvalidCredentials() throws Exception {
        UserLoginDto invalidLogin = new UserLoginDto();
        invalidLogin.setEmail("testemail@email.com");
        invalidLogin.setPassword("wrongPassword");
        UserLoginDto validLogin = new UserLoginDto();
        validLogin.setEmail("testemail@email.com");
        validLogin.setPassword("password");
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validLogin))).andExpect(status().isOk()).andReturn();
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)

            .content(objectMapper.writeValueAsString(invalidLogin))).andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidLogin))).andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidLogin))).andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidLogin))).andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidLogin))).andExpect(status().isForbidden()).andReturn();
        MvcResult mvcResult = mockMvc.perform(
            post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLogin))).andExpect(status().isForbidden()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String contentAsString = response.getContentAsString();
        Assertions.assertEquals("Account is locked due to multiple failed login attempts. Please try again later.",
            contentAsString);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void automaticallyLockedUserShouldBeUnlockedThroughCronJob() throws Exception {
        UserLoginDto invalidLogin = new UserLoginDto();
        invalidLogin.setEmail("testemail@email.com");
        invalidLogin.setPassword("wrongPassword");
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidLogin))).andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidLogin))).andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidLogin))).andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidLogin))).andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidLogin))).andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidLogin))).andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(post(TestData.AUTHENTICATION_BASE_URI).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidLogin))).andExpect(status().isForbidden()).andReturn();

        Thread.sleep(1000);

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = schedulerFactoryBean.getScheduler().getJobKeys(GroupMatcher.anyJobGroup()).stream()
            .filter(x -> Objects.equals(x.getName(), "unlockUser-testemail@email.com")).findFirst().get();
        List<Trigger> triggers = (List<Trigger>) schedulerFactoryBean.getScheduler().getTriggersOfJob(jobKey);
        assertThat(triggers.size()).isEqualTo(1);
        Trigger actual = triggers.get(0);
        assertThat(actual.getNextFireTime().before(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))).isTrue();
        assertThat(actual.getNextFireTime()
            .after(Date.from(Instant.now().plus(1, ChronoUnit.HOURS).minus(2, ChronoUnit.SECONDS)))).isTrue();

        TriggerKey triggerKey = new TriggerKey("unlockUserTrigger-testemail@email.com", "userTriggers");
        Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).startAt(Date.from(Instant.now()))
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
            .forJob("unlockUser-testemail@email.com", "userJobs").build();

        scheduler.rescheduleJob(triggerKey, newTrigger);

        Assertions.assertTrue(userRepository.findByEmail("testemail@email.com").isAccountLocked());

    }
}
