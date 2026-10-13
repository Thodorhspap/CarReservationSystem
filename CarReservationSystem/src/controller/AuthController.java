package controller;

import model.dao.UserDAO;
import model.entities.User;

import java.sql.SQLException;

public class AuthController {

    private final UserDAO userDAO;
    private User loggedInUser;

    public AuthController() {
        this.userDAO = new UserDAO();
    }

    // Επιστρέφει true αν το login πέτυχε, false αν τα credentials είναι λάθος.
    // Ρίχνει SQLException αν υπάρχει πρόβλημα με τη βάση.
    public boolean login(String username, String password) throws SQLException {
        User user = userDAO.getUserByCredentials(username, password);
        if (user != null) {
            this.loggedInUser = user;
            return true;
        }
        return false;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void logout() {
        this.loggedInUser = null;
    }
}
