package com.Vhytor.GoRent.services;

import com.Vhytor.GoRent.model.User;

import java.math.BigDecimal;
import java.util.Map;


public interface PaymentService {

    Map<String, String> initializeTransaction(User user, BigDecimal amount, Long homeId);
    boolean verifyTransaction(String reference);
}
