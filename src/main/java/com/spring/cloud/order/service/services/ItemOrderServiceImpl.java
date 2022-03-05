/*
 *  Copyright 2019 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.spring.cloud.order.service.services;

import com.spring.cloud.order.service.domain.ItemOrder;
import com.spring.cloud.order.service.domain.Customer;
import com.spring.cloud.order.service.domain.OrderStatusEnum;
import com.spring.cloud.order.service.repositories.ItemOrderRepository;
import com.spring.cloud.order.service.repositories.CustomerRepository;
import com.spring.cloud.order.service.web.mappers.ItemOrderMapper;
import com.spring.cloud.order.service.web.model.ItemOrderDto;
import com.spring.cloud.order.service.web.model.ItemOrderPagedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemOrderServiceImpl implements ItemOrderService {

    private final ItemOrderRepository ItemOrderRepository;
    private final CustomerRepository customerRepository;
    private final ItemOrderMapper ItemOrderMapper;
    private final ApplicationEventPublisher publisher;

    public ItemOrderServiceImpl(ItemOrderRepository ItemOrderRepository,
                                CustomerRepository customerRepository,
                                ItemOrderMapper ItemOrderMapper, ApplicationEventPublisher publisher) {
        this.ItemOrderRepository = ItemOrderRepository;
        this.customerRepository = customerRepository;
        this.ItemOrderMapper = ItemOrderMapper;
        this.publisher = publisher;
    }

    @Override
    public ItemOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Page<ItemOrder> ItemOrderPage =
                    ItemOrderRepository.findAllByCustomer(customerOptional.get(), pageable);

            return new ItemOrderPagedList(ItemOrderPage
                    .stream()
                    .map(ItemOrderMapper::ItemOrderToDto)
                    .collect(Collectors.toList()), PageRequest.of(
                    ItemOrderPage.getPageable().getPageNumber(),
                    ItemOrderPage.getPageable().getPageSize()),
                    ItemOrderPage.getTotalElements());
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public ItemOrderDto placeOrder(UUID customerId, ItemOrderDto ItemOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            ItemOrder ItemOrder = ItemOrderMapper.dtoToItemOrder(ItemOrderDto);
            ItemOrder.setId(null); //should not be set by outside client
            ItemOrder.setCustomer(customerOptional.get());
            ItemOrder.setOrderStatus(OrderStatusEnum.NEW);

            ItemOrder.getItemOrderLines().forEach(line -> line.setItemOrder(ItemOrder));

            ItemOrder savedItemOrder = ItemOrderRepository.saveAndFlush(ItemOrder);

            log.debug("Saved Item Order: " + ItemOrder.getId());

            //todo impl
          //  publisher.publishEvent(new NewItemOrderEvent(savedItemOrder));

            return ItemOrderMapper.ItemOrderToDto(savedItemOrder);
        }
        //todo add exception type
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public ItemOrderDto getOrderById(UUID customerId, UUID orderId) {
        return ItemOrderMapper.ItemOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        ItemOrder ItemOrder = getOrder(customerId, orderId);
        ItemOrder.setOrderStatus(OrderStatusEnum.PICKED_UP);

        ItemOrderRepository.save(ItemOrder);
    }

    private ItemOrder getOrder(UUID customerId, UUID orderId){
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if(customerOptional.isPresent()){
            Optional<ItemOrder> ItemOrderOptional = ItemOrderRepository.findById(orderId);

            if(ItemOrderOptional.isPresent()){
                ItemOrder ItemOrder = ItemOrderOptional.get();

                // fall to exception if customer id's do not match - order not for customer
                if(ItemOrder.getCustomer().getId().equals(customerId)){
                    return ItemOrder;
                }
            }
            throw new RuntimeException("Item Order Not Found");
        }
        throw new RuntimeException("Customer Not Found");
    }
}
