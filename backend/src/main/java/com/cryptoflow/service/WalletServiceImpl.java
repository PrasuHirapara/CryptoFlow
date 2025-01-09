package com.cryptoflow.service;

import com.cryptoflow.domain.OrderType;
import com.cryptoflow.entity.Order;
import com.cryptoflow.entity.User;
import com.cryptoflow.entity.Wallet;
import com.cryptoflow.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getId());

        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
        }

        return wallet;
    }

    @Override
    public Wallet addBalance(Wallet wallet, Long amount) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal newBalance = balance.add(balance);

        wallet.setBalance(newBalance);

        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findById(Long id) throws Exception {
        Optional<Wallet> wallet = walletRepository.findById(id);

        if(wallet.isPresent()) {
            return wallet.get();
        }

        throw new Exception("Wallet no found");
    }

    @Override
    public Wallet walletToWalletTransfer(User sender, Wallet receiver, Long amount) throws Exception {
        Wallet senderWallet = getUserWallet(sender);

        if(senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new Exception("Insufficient Balance...");
        }

//        for sender
        BigDecimal senderBalance = senderWallet
                .getBalance().
                subtract(BigDecimal.valueOf(amount));
        senderWallet.setBalance(senderBalance);
        walletRepository.save(senderWallet);

//        for receiver
        BigDecimal receiverBalance = receiver.getBalance().add(BigDecimal.valueOf(amount));
        receiver.setBalance(receiverBalance);
        walletRepository.save(receiver);

        return senderWallet;
    }

    @Override
    public Wallet payOrderPayment(Order order, User user) throws Exception {
        Wallet wallet = getUserWallet(user);

        if(order.getOrderType().equals(OrderType.BUY)) {
            BigDecimal newBalance = wallet.getBalance().subtract(order.getPrice());

            if(newBalance.compareTo(order.getPrice()) < 0) {
                throw new Exception("Insufficient fund for the transaction");
            }

            wallet.setBalance(newBalance);
            walletRepository.save(wallet);
        } else {
            BigDecimal newBalance = wallet.getBalance().add(order.getPrice());

            wallet.setBalance(newBalance);
            walletRepository.save(wallet);
        }

        return walletRepository.save(wallet);
    }
}
