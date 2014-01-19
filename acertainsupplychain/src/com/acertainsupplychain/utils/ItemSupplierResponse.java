package com.acertainsupplychain.utils;

import java.util.List;

import com.acertainsupplychain.business.ItemQuantity;

public class ItemSupplierResponse {

    public enum ItemSupplierResponseType { OK, FAIL, INVALID_ITEM,
                                           INVALID_SUPPLIER, INVALID_QUANTITY};
    public ItemSupplierResponseType type;

    public List<ItemQuantity> ordersPerItem;

}
