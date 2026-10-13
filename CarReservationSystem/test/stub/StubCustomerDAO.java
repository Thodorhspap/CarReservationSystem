package stub;

import model.dao.CustomerDAO;
import model.entities.Customer;

import java.sql.SQLException;

public class StubCustomerDAO extends CustomerDAO {

    // Αντιστοιχεί στο sample data του SQL script (user_id=3 → customer_id=1)
    private final Customer testCustomer =
            new Customer(1, 3, "Γιώργης Παπαδόπουλος", "giorgos@email.com", "6900000001");

    @Override
    public Customer getCustomerByUserId(int userId) throws SQLException {
        return (userId == 3) ? testCustomer : null;
    }
}
