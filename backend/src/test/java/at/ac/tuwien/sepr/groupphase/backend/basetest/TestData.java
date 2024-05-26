package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.util.Authority.Code;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface TestData {

    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String MESSAGE_BASE_URI = BASE_URI + "/messages";
    String USER_BASE_URI = BASE_URI + "/users";

    String ADMIN_USER = "admin@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add(Code.ADMIN);
            add(Code.USER);
        }
    };
    String ADMIN_USER_2 = "admin2@email.com";
    List<String> ADMIN_ROLES_2 = new ArrayList<>() {
        {
            add(Code.ADMIN);
            add(Code.USER);
        }
    };
    String DEFAULT_USER = "user@email.com";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add(Code.USER);
        }
    };
}
