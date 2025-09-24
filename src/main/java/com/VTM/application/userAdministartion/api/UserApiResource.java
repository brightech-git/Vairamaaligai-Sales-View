package com.VTM.application.userAdministartion.api;


import com.VTM.application.userAdministartion.dataOrModel.UserData;
import com.VTM.application.userAdministartion.dataOrModel.UserLoginData;
import com.VTM.application.userAdministartion.services.UserWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserApiResource {


    private final UserWritePlatformService userWritePlatformService;

    @Autowired
    public UserApiResource(UserWritePlatformService userWritePlatformService) {
        this.userWritePlatformService = userWritePlatformService;
    }


    @PostMapping("/user/register")
    public ResponseEntity<?> addUser(@RequestBody UserData userData) {
        try {
            UserData user = userWritePlatformService.addUser(userData);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);

        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> userLogin(@RequestBody UserLoginData userLoginData) {
        try {
            return userWritePlatformService.userLogin(userLoginData);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid User name or password");
        }
    }


}
