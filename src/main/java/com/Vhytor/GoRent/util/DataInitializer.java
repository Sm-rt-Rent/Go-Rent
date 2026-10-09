package com.Vhytor.GoRent.util;

import com.Vhytor.GoRent.enums.Role;
import com.Vhytor.GoRent.model.Home;
import com.Vhytor.GoRent.model.User;
import com.Vhytor.GoRent.repositories.HomeRepository;
import com.Vhytor.GoRent.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final HomeRepository homeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(HomeRepository homeRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.homeRepository = homeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Ensure we have at least one Landlord
        User landlord = userRepository.findByUserEmail("landlord@smartrent.com")
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUserEmail("landlord@smartrent.com");
                    newUser.setPassword(passwordEncoder.encode("admin123"));
                    newUser.setFullName("Default Landlord");
                    newUser.setRole(Role.LANDLORD);
                    return userRepository.save(newUser);
                });

        // 2. Only add homes if the table is empty
        if (homeRepository.count() == 0) {
            Home home1 = new Home();
            home1.setAddress("123 Silicon Valley Way, Lagos");
            home1.setDescription("Luxury 2-bedroom apartment with smart home features.");
            home1.setPricePerMonth(new BigDecimal("250000.00"));
            home1.setViewingFee(new BigDecimal("2000.00"));
            home1.setSmartLockCode("9999");
            home1.setLandlord(landlord); // LINKED TO LANDLORD

            Home home2 = new Home();
            home2.setAddress("45 Ocean View Drive, Lagos");
            home2.setDescription("Cozy studio for professionals.");
            home2.setPricePerMonth(new BigDecimal("120000.00"));
            home2.setViewingFee(new BigDecimal("1000.00"));
            home2.setSmartLockCode("1234");
            home2.setLandlord(landlord); // LINKED TO LANDLORD

            homeRepository.save(home1);
            homeRepository.save(home2);
            System.out.println("SmartRent: Landlord and properties initialized.");
        }
    }
}


//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private final HomeRepository homeRepository;
//    private final UserRepository userRepository;
//
//    public DataInitializer(HomeRepository homeRepository, UserRepository userRepository) {
//        this.homeRepository = homeRepository;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        if (homeRepository.count() == 0) {
//            // House 1: Modern Apartment
//            Home home1 = new Home();
//            home1.setAddress("123 Silicon Valley Way, Lagos");
//            home1.setDescription("Luxury 2-bedroom apartment with smart home features.");
//            home1.setPricePerMonth(new BigDecimal("250000.00"));
//            home1.setViewingFee(new BigDecimal("2000.00")); // The "little token"
//            home1.setSmartLockCode("9999"); // Master code for testing
//
//            // House 2: Cozy Studio
//            Home home2 = new Home();
//            home2.setAddress("45 Ocean View Drive, Lagos");
//            home2.setDescription("Compact studio perfect for young professionals.");
//            home2.setPricePerMonth(new BigDecimal("120000.00"));
//            home2.setViewingFee(new BigDecimal("1000.00"));
//            home2.setSmartLockCode("1234");
//
//            homeRepository.save(home1);
//            homeRepository.save(home2);
//
//            System.out.println("SmartRent: Default homes have been added to MySQL.");
//        }
//    }
//}