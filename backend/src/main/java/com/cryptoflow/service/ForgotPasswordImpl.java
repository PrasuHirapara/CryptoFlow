package com.cryptoflow.service;

import com.cryptoflow.domain.VerificationType;
import com.cryptoflow.entity.ForgotPasswordToken;
import com.cryptoflow.entity.User;
import com.cryptoflow.repository.ForgotPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgotPasswordImpl implements ForgotPasswordService{

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Override
    public ForgotPasswordToken createToken(User user,
                                           String id, String otp,
                                           VerificationType verificationType,
                                           String sendTo
    ) {
        ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
        forgotPasswordToken.setUser(user);
        forgotPasswordToken.setSendTo(sendTo);
        forgotPasswordToken.setVerificationType(verificationType);
        forgotPasswordToken.setOtp(otp);
        forgotPasswordToken.setId(id);

        return forgotPasswordRepository.save(forgotPasswordToken);
    }

    @Override
    public ForgotPasswordToken findById(String id) {
        Optional<ForgotPasswordToken> forgotPasswordToken = forgotPasswordRepository.findById(id);

        return forgotPasswordToken.orElse(null);

    }

    @Override
    public ForgotPasswordToken findByUser(Long id) {
        return forgotPasswordRepository.findByUserId(id);
    }

    @Override
    public void deleteToken(ForgotPasswordToken token) {
        forgotPasswordRepository.delete(token);
    }
}
