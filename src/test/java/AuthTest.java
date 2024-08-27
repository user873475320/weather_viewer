import dao.SessionDAO;
import dto.UserDTO;
import dto.UserLoginDTO;
import entity.User;
import exception.server.DatabaseInteractionException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import service.AuthenticationService;
import service.SessionService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthTest {

    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final AuthenticationService authenticationService = new AuthenticationService();
    private final SessionService sessionService = new SessionService();
    private final SessionDAO sessionDAO = new SessionDAO();

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .login(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .build();
        userDTO = new UserLoginDTO(user.getLogin(), user.getPassword());
    }

    @AfterEach
    void tearDown() {
        try (Session hibernateSession = sessionFactory.openSession()) {
            Transaction tx = hibernateSession.beginTransaction();
            hibernateSession.createQuery("delete from User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    void givenValidUser_whenSaveUser_thenUserIsSaved() {
        authenticationService.saveUser(userDTO);

        try (Session hibernateSession = sessionFactory.openSession()) {
            Transaction tx = hibernateSession.beginTransaction();

            User fetchedUser = (User) hibernateSession
                    .createQuery("select u from User u where u.login = :login")
                    .setParameter("login", user.getLogin())
                    .uniqueResult();

            tx.commit();

            assertThat(fetchedUser).isNotNull();
            assertThat(fetchedUser.getLogin()).isEqualTo(user.getLogin());
            assertThat(passwordEncoder.matches(user.getPassword(), fetchedUser.getPassword())).isTrue();
        }
    }

    @Test
    void givenAlreadyExistingUser_whenSaveUser_thenThrowException() {
        try (Session hibernateSession = sessionFactory.openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();
            hibernateSession.persist(user);
            transaction.commit();
        }

        UserDTO newUserDTO = new UserLoginDTO(user.getLogin(), UUID.randomUUID().toString());

        assertThatThrownBy(() -> authenticationService.saveUser(newUserDTO))
                .isInstanceOf(DatabaseInteractionException.class);
    }

    @Test
    void testSessionExpirationAfterSavingAndFetching() throws InterruptedException {
        // Save user to DB
        try (Session hibernateSession = sessionFactory.openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();
            hibernateSession.persist(user);
            transaction.commit();
        }

        // Fetch user from DB
        User fetchedUser;
        try (Session hibernateSession = sessionFactory.openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();

            Optional<User> optionalUser = hibernateSession
                    .createQuery("from User where login = :login", User.class)
                    .setParameter("login", user.getLogin())
                    .uniqueResultOptional();

            assertThat(optionalUser).isPresent();
            fetchedUser = optionalUser.get();

            transaction.commit();
        }

        // Get configured session with expires time = current time plus 5 sec
        entity.Session session = sessionService.getConfiguredSession(fetchedUser.getId());
        session.setExpiresAt(LocalDateTime.now().plusSeconds(5));

        // Save this session in DB
        try (Session hibernateSession = sessionFactory.openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();
            hibernateSession.persist(session);
            transaction.commit();
        }

        // Get session from DB using our session service
        Optional<entity.Session> optionalSession = sessionDAO.findSessionWithLoadedUserById(session.getId());

        assertThat(optionalSession).isPresent();
        assertThat(sessionService.checkIfSessionIsValid(optionalSession.get())).isTrue();

        // Wait until the session becomes expired
        Thread.sleep(7000);

        assertThat(sessionService.checkIfSessionIsValid(optionalSession.get())).isFalse();
    }
}