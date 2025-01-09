package com.cryptoflow.service;

import com.cryptoflow.entity.Order;
import com.cryptoflow.entity.User;
import com.cryptoflow.entity.Wallet;

public interface WalletService {

    Wallet getUserWallet(User user);

    Wallet addBalance(Wallet wallet, Long amount);

    Wallet findById(Long id) throws Exception;

    Wallet walletToWalletTransfer(User sender, Wallet receiver, Long amount) throws Exception;

    Wallet payOrderPayment(Order order, User user) throws Exception;
}
