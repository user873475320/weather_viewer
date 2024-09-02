package util;

import dto.UserDTO;
import entity.Session;
import entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import service.SessionService;
import service.UserService;

import java.util.Optional;

@Slf4j
public class SessionHandler {
    private static final SessionService sessionService = new SessionService();
    private static final UserService userService = new UserService();

    private SessionHandler() {}

    public static void handleWorkWithSessionAndCookie(HttpServletRequest req, HttpServletResponse resp, UserDTO userDTO) {
        log.info("Start work with session and cookie");
        // Get User entity object from DB using UserDTO
        Optional<User> authorizedUser = userService.findUserByLoginAndPassword(userDTO.getLogin(), userDTO.getPassword());

        // Get configured session entity object
        Session session = sessionService.getConfiguredSession(authorizedUser.orElseThrow().getId());
        session.setUser(authorizedUser.orElseThrow());
        log.debug("Got configured session {} of the user with ID: {}", session, session.getUser().getId());

        // Save the session to DB
        sessionService.save(session);

        // Add our session and user objects to HttpSession
        HttpSessionUtils.createAndSetUpHttpSession(req, session);

        // Create cookie with sessionId from the previous session
        resp.addCookie(CookieUtils.createConfiguredCookie(session.getId()));
        log.debug("Added our session with UUID: {} into a cookie", session.getId());

        log.info("Finish work with session and cookie");
    }
}