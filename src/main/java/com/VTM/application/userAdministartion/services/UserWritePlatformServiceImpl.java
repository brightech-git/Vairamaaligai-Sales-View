package com.VTM.application.userAdministartion.services;

import com.VTM.application.server.JwtTokenConfig.JwtTokenUtil;
import com.VTM.application.userAdministartion.dataOrModel.UserData;
import com.VTM.application.userAdministartion.dataOrModel.UserLoginData;
import com.VTM.application.userAdministartion.entityOrDomain.User;
import com.VTM.application.userAdministartion.exceptions.InvalidContactNumberException;
import com.VTM.application.userAdministartion.exceptions.InvalidEmailException;
import com.VTM.application.userAdministartion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserWritePlatformServiceImpl implements UserWritePlatformService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserWritePlatformServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, CustomUserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @Override
    public UserData addUser(UserData userData) {

        User user = new User();
        user.setUsername(userData.getUsername() != null ? userData.getUsername() : user.getUsername());


        if (userData.getEmail() != null && !userData.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new InvalidEmailException("Invalid email format: " + userData.getEmail());
        }

        user.setEmail(userData.getEmail() != null ? userData.getEmail() : user.getEmail());

        if (userData.getContactNumber() != null && userData.getContactNumber().length() != 10) {
            throw new InvalidContactNumberException("Contact number must be exactly 10 digits");
        }

        // Check Contact Number Uniqueness
        if (userData.getContactNumber() != null && userRepository.existsByContactNumber(userData.getContactNumber())) {
            throw new InvalidContactNumberException("Contact number must be unique");
        }

        user.setContactNumber(userData.getContactNumber() != null ? userData.getContactNumber() : user.getContactNumber());
        user.setPassword(passwordEncoder.encode(userData.getPassword()) != null ? passwordEncoder.encode(userData.getPassword()) : user.getPassword());

        User savedUser = userRepository.save(user);
        return convertUserToUserData(savedUser);
    }

//    @Override
//    public ResponseEntity<?> userLogin(UserLoginData userLoginData) {
//
//        String usernameOrEmail = userLoginData.getUsernameOrEmail();
//        String password = userLoginData.getPassword();
//
//        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
//
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
//        }
//
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password"); // Error message for invalid credentials
//        }
//
//
//        Long userId = user.getId();
//        User userData = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException
//                ("user not found with ID: " + userId));
//
////        UserData userLogin = new UserData();
////        userData.setId(userLogin.getId());
////        userData.setUsername(userLogin.getUsername());
////        userData.setEmail(userLogin.getEmail());
//
//        //Generate token and create response
//        UserDetails userDetails = userDetailsService.loadUserByUsername(usernameOrEmail);
//        String token = jwtTokenUtil.generateToken(userDetails);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//        try{
//            String userDataJson = objectMapper.writeValueAsString(userData);
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "User Login Successfully!!!");
//            response.put("token", token);
//            response.put("id", user.getId());
//            response.put("username", user.getUsername());
//            response.put("email", user.getEmail());
////            response.put("User Data", objectMapper.readValue(userDataJson, Map.class));
//            return ResponseEntity.ok(response);
//
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

@Override
public ResponseEntity<?> userLogin(UserLoginData userLoginData) {
    String credential = userLoginData.getUsernameOrEmail();
    String password   = userLoginData.getPassword();

    // Find user by username, email, or contact number
    User user = userRepository.findByUsernameOrEmailOrContactNumber(
            credential,
            credential,
            credential
    );

    if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username, email or mobile number—or wrong password");
    }

    // Generate JWT token with full user details
    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
    String token = jwtTokenUtil.generateToken(userDetails, user.getId(), user.getEmail(), user.getContactNumber());

    // Prepare response
    Map<String, Object> response = new HashMap<>();
    response.put("message",  "User login successful");
    response.put("token",    token);
    response.put("id",       user.getId());
    response.put("username", user.getUsername());
    response.put("email",    user.getEmail());
    response.put("mobile",   user.getContactNumber());

    return ResponseEntity.ok(response);
}

    private UserData convertUserToUserData(User savedUser) {
        return new UserData(
                savedUser.getUsername(),

                savedUser.getEmail(),
                savedUser.getContactNumber()
        );
    }
}
