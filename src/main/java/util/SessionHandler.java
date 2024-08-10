package util;

import dto.UserDTO;
import entity.Session;
import entity.User;
import service.AuthenticationService;
import service.SessionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class SessionHandler {
    private static final SessionService sessionService = new SessionService();
    private static final AuthenticationService authenticationService = new AuthenticationService();

    private SessionHandler() {
    }

    public static void handleWorkWithSessionAndCookie(HttpServletRequest req, HttpServletResponse resp, UserDTO userDTO) {
        // Get User entity object from DB using UserDTO
        Optional<User> authorizedUser = authenticationService.findUserByLoginAndPassword(userDTO.getLogin(), userDTO.getPassword());

        // Get configured session entity object
        Session session = sessionService.getConfiguredSession(authorizedUser.orElseThrow().getId());

        // Save the session to DB
        sessionService.saveSession(session);

        // Add our session and user objects to HttpSession
        HttpSessionUtils.createAndSetUpHttpSession(req, session, authorizedUser.get());

        // Create cookie with sessionId from the previous session
        resp.addCookie(CookieUtils.createConfiguredCookie(session.getId()));
    }
}