package com.client.ws.rasmooplus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.client.ws.rasmooplus.domain.service.impl.UserCredentialsServiceImpl;
import com.client.ws.rasmooplus.dto.UserCredentialsDto;
import com.client.ws.rasmooplus.dto.UserRecoveryCodeDto;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/user-recovery-code")
public class UserRecoveryCodeController {

    @Autowired
    UserCredentialsServiceImpl userCredentialsServiceImpl;

    @PostMapping("/send")
    public ResponseEntity<Void> sendRecoveryCode(@RequestBody @Valid UserRecoveryCodeDto dto) {
        userCredentialsServiceImpl.sendRecoveryCode(dto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/valid")
    public ResponseEntity<Boolean> isRecoveryCodeValid(@RequestParam("recoveryCode") String recoveryCode,
            @RequestParam("email") String email) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userCredentialsServiceImpl.isRecoveryCodeValid(recoveryCode, email));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePasswordByRecoveryCode(@RequestBody @Valid UserCredentialsDto dto,
            @PathParam("recoveryCode") String recoveryCode) {

        userCredentialsServiceImpl.updatePasswordByRecoveryCode(dto, recoveryCode);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
