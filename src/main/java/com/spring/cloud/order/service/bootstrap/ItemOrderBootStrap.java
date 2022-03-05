package com.spring.cloud.order.service.bootstrap;

import com.spring.cloud.order.service.domain.Customer;
import com.spring.cloud.order.service.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by jt on 2019-06-06.
 */
@RequiredArgsConstructor
@Component
public class ItemOrderBootStrap implements CommandLineRunner {
    public static final String GROCERY_STORE = "Grocery Store";
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadCustomerData();
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            customerRepository.save(Customer.builder()
                    .customerName(GROCERY_STORE)
                    .apiKey(UUID.randomUUID())
                    .build());
        }
    }
}
