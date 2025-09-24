package com.VTM.application.userAdministartion.services;


import com.VTM.application.userAdministartion.dataOrModel.UserData;
import com.VTM.application.userAdministartion.dataOrModel.UserLoginData;
import org.springframework.http.ResponseEntity;

public interface UserWritePlatformService {

    UserData addUser(UserData userData);

    ResponseEntity<?> userLogin(UserLoginData userLoginData);
}
