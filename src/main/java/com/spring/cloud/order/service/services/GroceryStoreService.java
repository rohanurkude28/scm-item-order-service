package com.spring.cloud.order.service.services;

import com.spring.cloud.order.service.bootstrap.ItemOrderBootStrap;
import com.spring.cloud.order.service.domain.Customer;
import com.spring.cloud.order.service.repositories.ItemOrderRepository;
import com.spring.cloud.order.service.repositories.CustomerRepository;
import com.spring.cloud.order.service.web.model.ItemOrderDto;
import com.spring.cloud.order.service.web.model.ItemOrderLineDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class GroceryStoreService {

    private final CustomerRepository customerRepository;
    private final ItemOrderService ItemOrderService;
    private final ItemOrderRepository ItemOrderRepository;
    private final List<String> ItemUpcs = new ArrayList<>(3);

    public GroceryStoreService(CustomerRepository customerRepository, ItemOrderService ItemOrderService,
                              ItemOrderRepository ItemOrderRepository) {
        this.customerRepository = customerRepository;
        this.ItemOrderService = ItemOrderService;
        this.ItemOrderRepository = ItemOrderRepository;

        ItemUpcs.add(ItemOrderBootStrap.Item_1_UPC);
        ItemUpcs.add(ItemOrderBootStrap.Item_2_UPC);
        ItemUpcs.add(ItemOrderBootStrap.Item_3_UPC);
    }

    @Transactional
    @Scheduled(fixedRate = 2000) //run every 2 seconds
    public void placeTastingRoomOrder(){

        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(ItemOrderBootStrap.TASTING_ROOM);

        if (customerList.size() == 1){ //should be just one
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few tasting room customers found");
        }
    }

    private void doPlaceOrder(Customer customer) {
        String ItemToOrder = getRandomItemUpc();

        ItemOrderLineDto ItemOrderLine = ItemOrderLineDto.builder()
                .upc(ItemToOrder)
                .orderQuantity(new Random().nextInt(6)) //todo externalize value to property
                .build();

        List<ItemOrderLineDto> ItemOrderLineSet = new ArrayList<>();
        ItemOrderLineSet.add(ItemOrderLine);

        ItemOrderDto ItemOrder = ItemOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .ItemOrderLines(ItemOrderLineSet)
                .build();

        ItemOrderDto savedOrder = ItemOrderService.placeOrder(customer.getId(), ItemOrder);

    }

    private String getRandomItemUpc() {
        return ItemUpcs.get(new Random().nextInt(ItemUpcs.size() -0));
    }
}
