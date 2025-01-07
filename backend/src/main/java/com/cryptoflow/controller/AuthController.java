package com.cryptoflow.controller;

import com.cryptoflow.config.JwtProvider;
import com.cryptoflow.domain.VerificationType;
import com.cryptoflow.entity.ForgotPasswordToken;
import com.cryptoflow.entity.TwoFactorOTP;
import com.cryptoflow.entity.User;
import com.cryptoflow.entity.VerificationCode;
import com.cryptoflow.repository.UserRepository;
import com.cryptoflow.request.ForgotPasswordTokenRequest;
import com.cryptoflow.request.ResetPasswordRequest;
import com.cryptoflow.response.ApiResponse;
import com.cryptoflow.response.AuthResponse;
import com.cryptoflow.service.*;
import com.cryptoflow.utils.OTPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private TwoFactorOTPService twoFactorOTPService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody User user) throws Exception {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User isEmailExist = userService.findByEmail(user.getEmail());
        if (isEmailExist != null) {
            throw new Exception("Email is already in use");
        }

        User savedUser = userService.saveUser(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("Success");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody User user) throws Exception {

        String email = user.getEmail();
        String password = user.getPassword();

        Authentication auth =  authenticate(email, password);

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        User authUser = userRepository.findByEmail(email);

        if(user.getTwoFactorAuth().isEnabled()) {
            AuthResponse res = new AuthResponse();
            res.setMessage("2 factor is enabled");
            res.setTwoFactorAuthEnabled(true);

            String otp = OTPUtils.generateOTP();

            TwoFactorOTP oldTwoFactorOtp = twoFactorOTPService.findByUser(authUser.getId());
            if(oldTwoFactorOtp != null) {
                twoFactorOTPService.deleteTwoFactorOTP(oldTwoFactorOtp);
            }

            TwoFactorOTP newTwoFactorOtp = twoFactorOTPService.createTwoFactorOTP(authUser, otp, jwt);

            emailService.sendVerificationOtpMail(email, otp);

            res.setSession(newTwoFactorOtp.getId());

            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        }

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("Login Success");

        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    @PostMapping("/two-factor/opt/{otp}")
    public ResponseEntity<AuthResponse> verifySigninOtp(
            @PathVariable String otp,
            @RequestParam String id
    ) throws Exception {
        TwoFactorOTP twoFactorOTP = twoFactorOTPService.findById(id);

        if(twoFactorOTPService.verifyTwoFactorOTP(twoFactorOTP, otp)) {
            AuthResponse res = new AuthResponse();

            res.setMessage("2 factor authentication is verified");
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOTP.getJwt());

            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            throw new Exception("Invalid Otp");
        }
    }

    @PostMapping("/api/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(
            @RequestBody ForgotPasswordTokenRequest request
    ) throws Exception {

        User user = userService.findByEmail(request.getSendTo());
        String otp = OTPUtils.generateOTP();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        ForgotPasswordToken token = forgotPasswordService.findByUser(user.getId());

        if(token == null) {
            token = forgotPasswordService.createToken(user, id, otp, request.getVerificationType(), request.getSendTo());
        }

        if(request.getVerificationType().equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOtpMail(user.getEmail(), token.getOtp());
        }

        AuthResponse res = new AuthResponse();
        res.setSession(token.getId());
        res.setStatus(true);
        res.setMessage("Password reset otp sent successfully");

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping("/api/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestParam String id,
            @RequestBody ResetPasswordRequest req,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findById(id);

        boolean isVerified = forgotPasswordToken.getOtp().equals(req.getOtp());

        if(isVerified) {
            userService.updatePassword(forgotPasswordToken.getUser(), req.getPassword());

            ApiResponse res = new ApiResponse();
            res.setMessage("Password updated Successfully");

            return new ResponseEntity<>(res, HttpStatus.OK);
        }

        throw new Exception("Invalid Otp");
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(email);

        if(userDetails == null) {
            throw new BadCredentialsException("invalid email or password");
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
}