package com.VTM.application.mpin;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MpinService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MpinService(@Qualifier("primaryJdbcTemplate") JdbcTemplate jdbcTemplate,
                       PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Save the hashed MPIN to the database using user ID linked by contact number.
     */
    public boolean saveMpin(String contactNumber, String hashedMpin, String username, String email) {
        try {
            // Get user ID from ecom_user
            String getUserIdSql = "SELECT id FROM Sales_View WHERE contact_number = ?";
            Long userId = jdbcTemplate.queryForObject(getUserIdSql, new Object[]{contactNumber}, Long.class);

            // Check if MPIN already exists for this user
            String checkSql = "SELECT COUNT(*) FROM Dashboard_Mpin WHERE id = ?";
            int count = jdbcTemplate.queryForObject(checkSql, new Object[]{userId}, Integer.class);
            if (count > 0) {
                return false;
            }

            // Insert MPIN record
            String insertSql = "INSERT INTO Dashboard_Mpin (id, contact_number, mpin, username, email, failed_attempts) VALUES (?, ?, ?, ?, ?, 0)";
            int rows = jdbcTemplate.update(insertSql, userId, contactNumber, hashedMpin, username, email);
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if an MPIN exists for a contact number.
     */
    public boolean doesMpinExist(String contactNumber) {
        try {
            String getUserIdSql = "SELECT id FROM Sales_View WHERE contact_number = ?";
            Long userId = jdbcTemplate.queryForObject(getUserIdSql, new Object[]{contactNumber}, Long.class);

            String sql = "SELECT COUNT(*) FROM Dashboard_Mpin WHERE id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{userId}, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the stored hashed MPIN for a contact number.
     */
    public String getMpinHash(String contactNumber) {
        try {
            String getUserIdSql = "SELECT id FROM Sales_View WHERE contact_number = ?";
            Long userId = jdbcTemplate.queryForObject(getUserIdSql, new Object[]{contactNumber}, Long.class);

            String sql = "SELECT mpin FROM Dashboard_Mpin WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Reset the MPIN for a contact number.
     */
    public boolean resetMpin(String contactNumber, String newHashedMpin) {
        try {
            String getUserIdSql = "SELECT id FROM Sales_View WHERE contact_number = ?";
            Long userId = jdbcTemplate.queryForObject(getUserIdSql, new Object[]{contactNumber}, Long.class);

            String sql = "UPDATE Dashboard_Mpin SET mpin = ? WHERE id = ?";
            return jdbcTemplate.update(sql, newHashedMpin, userId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Increment failed login attempts.
     */
    public int incrementFailedAttempts(String contactNumber) {
        try {
            String getUserIdSql = "SELECT id FROM Sales_View WHERE contact_number = ?";
            Long userId = jdbcTemplate.queryForObject(getUserIdSql, new Object[]{contactNumber}, Long.class);

            String updateSql = "UPDATE Dashboard_Mpin SET failed_attempts = failed_attempts + 1 WHERE id = ?";
            jdbcTemplate.update(updateSql, userId);

            String querySql = "SELECT failed_attempts FROM Dashboard_Mpin WHERE id = ?";
            return jdbcTemplate.queryForObject(querySql, new Object[]{userId}, Integer.class);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Reset failed login attempts.
     */
    public boolean resetFailedAttempts(String contactNumber) {
        try {
            String getUserIdSql = "SELECT id FROM Sales_View WHERE contact_number = ?";
            Long userId = jdbcTemplate.queryForObject(getUserIdSql, new Object[]{contactNumber}, Long.class);

            String sql = "UPDATE Dashboard_Mpin SET failed_attempts = 0 WHERE id = ?";
            return jdbcTemplate.update(sql, userId) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
