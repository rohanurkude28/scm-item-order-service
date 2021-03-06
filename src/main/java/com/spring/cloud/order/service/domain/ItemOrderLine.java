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
package com.spring.cloud.order.service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ItemOrderLine extends BaseEntity {

    @Builder
    public ItemOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                         ItemOrder itemOrder, UUID itemId, Integer orderQuantity,
                         Integer quantityAllocated,Long batchNo) {
        super(id, version, createdDate, lastModifiedDate);
        this.itemOrder = itemOrder;
        this.itemId = itemId;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
        this.batchNo=batchNo;
    }

    @ManyToOne
    private ItemOrder itemOrder;

    private UUID itemId;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;
    private Long batchNo;
}
