package com.acertainsupplychain.utils;

import java.util.Set;

import com.acertainsupplychain.business.OrderStep;

public class ItemSupplierRequest {

    public enum ItemSupplierRequestType { EXECUTE_STEP, GET_ORDERS_PER_ITEM };
    public ItemSupplierRequestType type;
    public OrderStep step;
    public Set<Integer> itemIds;

}
