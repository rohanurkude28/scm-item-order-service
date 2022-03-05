package com.spring.cloud.order.service.web.mappers;

import com.spring.cloud.order.service.domain.ItemOrderLine;
import com.spring.cloud.order.service.web.model.ItemOrderLineDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface ItemOrderLineMapper {
    ItemOrderLineDto ItemOrderLineToDto(ItemOrderLine line);

    ItemOrderLine dtoToItemOrderLine(ItemOrderLineDto dto);
}
