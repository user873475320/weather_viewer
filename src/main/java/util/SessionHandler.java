package util;

import dto.UserDTO;
import entity.Session;
import entity.User;
import service.SessionService;
import service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class SessionHandler {
    private static final SessionService sessionService = new SessionService();
    private static final UserService userService = new UserService();

    private SessionHandler() {}

    public static void handleWorkWithSessionAndCookie(HttpServletRequest req, HttpServletResponse resp, UserDTO userDTO) {
        // Get User entity object from DB using UserDTO
        Optional<User> authorizedUser = userService.findUserByLoginAndPassword(userDTO.getLogin(), userDTO.getPassword());

        // Get configured session entity object
        Session session = sessionService.getConfiguredSession(authorizedUser.orElseThrow().getId());
        session.setUser(authorizedUser.orElseThrow());

        // Save the session to DB
        sessionService.save(session);

        // Add our session and user objects to HttpSession
        HttpSessionUtils.createAndSetUpHttpSession(req, session);

        // Create cookie with sessionId from the previous session
        resp.addCookie(CookieUtils.createConfiguredCookie(session.getId()));
    }
}