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
    public static final String TASTING_ROOM = "Grocery Store";
    public static final String Item_1_UPC = "0631234200036";
    public static final String Item_2_UPC = "0631234300019";
    public static final String Item_3_UPC = "0083783375213";

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadCustomerData();
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            customerRepository.save(Customer.builder()
                    .customerName(TASTING_ROOM)
                    .apiKey(UUID.randomUUID())
                    .build());
        }
    }
}
