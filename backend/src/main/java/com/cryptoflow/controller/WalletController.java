package com.cryptoflow.controller;

import com.cryptoflow.entity.Order;
import com.cryptoflow.entity.User;
import com.cryptoflow.entity.Wallet;
import com.cryptoflow.entity.WalletTransaction;
import com.cryptoflow.service.UserService;
import com.cryptoflow.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;



    @GetMapping
    public ResponseEntity<Wallet> getUserWallet(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findByJWT(jwt);

        Wallet wallet = walletService.getUserWallet(user);

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{walletId}/transfer")
    public ResponseEntity<Wallet> walletToWalletTransfer(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long walletId,
            @RequestBody WalletTransaction req
            ) throws Exception {

        User senderUser = userService.findByJWT(jwt);
        Wallet receiverWallet = walletService.findById(walletId);

        Wallet walletOfSender = walletService.walletToWalletTransfer(senderUser, receiverWallet, req.getAmount());

        return new ResponseEntity<>(walletOfSender, HttpStatus.ACCEPTED);
    }

//    @PutMapping("/order/{orderId}/pay")
//    public ResponseEntity<Wallet> payOrderPayment(
//            @RequestHeader("Authorization") String jwt,
//            @PathVariable Long orderId
//    ) throws Exception {
//
//        User user = userService.findByJWT(jwt);
//
//        Order order = orderService.getOrderById(orderId);
//
//        Wallet wallet = walletService.payOrderPayment(order, user);
//
//        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
//    }
}
