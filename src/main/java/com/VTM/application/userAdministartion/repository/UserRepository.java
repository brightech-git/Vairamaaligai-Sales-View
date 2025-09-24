package com.VTM.application.userAdministartion.repository;


import com.VTM.application.userAdministartion.entityOrDomain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameOrEmail(String username, String email);

    // New: also search by contact number
    User findByUsernameOrEmailOrContactNumber(String username, String email, String contactNumber);

    boolean existsByContactNumber(String contactNumber);
}
