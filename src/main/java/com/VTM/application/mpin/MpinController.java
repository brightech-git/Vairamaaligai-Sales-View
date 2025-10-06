package com.VTM.application.mpin;




import com.VTM.application.server.JwtTokenConfig.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;

@RestController
@CrossOrigin(origins = {
        "https://nkvairamaaligai.com/",
        "http://localhost:3000"
})
@RequestMapping("/api/v1")
@Validated
public class MpinController {

    private final JwtTokenUtil jwtTokenUtil;
    private final MpinService mpinService;
    private final PasswordEncoder passwordEncoder;

    // Constructor for injecting JwtTokenUtil, MpinService, and PasswordEncoder
    @Autowired
    public MpinController(JwtTokenUtil jwtTokenUtil, MpinService mpinService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.mpinService = mpinService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * API to create and store MPIN securely, along with username and email.
     */
    @PostMapping("/create")
    public ResponseEntity<String> createMpin(@RequestHeader("Authorization") String token,
                                             @RequestParam String mpin) {
        String jwt = token.replace("Bearer ", "");
        String contactNumber = jwtTokenUtil.extractContact(jwt);
        String username = jwtTokenUtil.extractUsername(jwt);
        String email = jwtTokenUtil.extractEmail(jwt);

        // Check if MPIN already exists
        if (mpinService.getMpinHash(contactNumber) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("MPIN already created. Please use reset/forgot MPIN.");
        }

        String hashedMpin = passwordEncoder.encode(mpin);
        boolean saved = mpinService.saveMpin(contactNumber, hashedMpin, username, email);

        return saved
                ? ResponseEntity.ok("MPIN created successfully.")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving MPIN.");
    }

    /**
     * API to verify if the entered MPIN matches the stored MPIN.
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyMpin(@RequestHeader("Authorization") String token,
                                             @RequestParam @NotEmpty String enteredMpin) {
        String contactNumber = jwtTokenUtil.extractContact(token.replace("Bearer ", ""));
        String storedMpinHash = mpinService.getMpinHash(contactNumber);

        if (storedMpinHash == null || storedMpinHash.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MPIN not found for user.");
        }

        return passwordEncoder.matches(enteredMpin, storedMpinHash)
                ? ResponseEntity.ok("MPIN verified successfully.")
                : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid MPIN.");
    }

    /**
     * API to reset the MPIN (for cases like user forgot MPIN, etc.).
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetMpin(@RequestHeader("Authorization") String token,
                                            @RequestParam @NotEmpty String newMpin) {
        String contactNumber = jwtTokenUtil.extractContact(token.replace("Bearer ", ""));
        String hashedMpin = passwordEncoder.encode(newMpin);

        boolean resetSuccessful = mpinService.resetMpin(contactNumber, hashedMpin);
        return resetSuccessful
                ? ResponseEntity.ok("MPIN reset successfully.")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resetting MPIN.");
    }

    /**
     * API to increment failed login attempt counter for a user.
     */
    @PostMapping("/increment-failed-attempts")
    public ResponseEntity<String> incrementFailedAttempts(@RequestParam @NotEmpty String contactNumber) {
        // Increment failed attempts for the given contact number
        int failedAttempts = mpinService.incrementFailedAttempts(contactNumber);

        return ResponseEntity.ok("Failed attempts incremented. Current count: " + failedAttempts);
    }

    /**
     * API to reset failed login attempt counter for a user.
     */
    @PostMapping("/reset-failed-attempts")
    public ResponseEntity<String> resetFailedAttempts(@RequestParam @NotEmpty String contactNumber) {
        // Reset failed attempts for the given contact number
        boolean resetSuccessful = mpinService.resetFailedAttempts(contactNumber);

        if (resetSuccessful) {
            return ResponseEntity.ok("Failed attempts reset successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resetting failed attempts.");
        }
    }
}






