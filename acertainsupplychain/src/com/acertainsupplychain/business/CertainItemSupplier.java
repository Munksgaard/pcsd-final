package com.acertainsupplychain.business;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import com.acertainsupplychain.interfaces.ItemSupplier;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.InvalidItemException;
import com.acertainsupplychain.utils.LogException;
import com.acertainsupplychain.utils.Logger;
import com.acertainsupplychain.utils.SupplyChainUtility;

/**
 * The CertainItemSupplier class is the basic implementation of the
 * ItemSupplier interface. It maintains a set of items and the total
 * quantity orderes for those items. We assume that the set of items
 * managed is fixed.
 */
public class CertainItemSupplier implements ItemSupplier {

    private final int supplierId;
    private final Logger logger;

    private Map<Integer, Integer> itemQuantities;

    public CertainItemSupplier(int supplierId, Set<Integer> itemIds)
          throws OrderProcessingException {
        this.supplierId = supplierId;

        try {
            this.logger = new Logger("ItemSupplier" + supplierId + ".log");
        } catch (LogException e) {
            e.getException().printStackTrace();
            throw new OrderProcessingException();
        }

        itemQuantities = new HashMap<Integer, Integer>();
        for (Integer itemId : itemIds) {
            itemQuantities.put(itemId, 0);
        }
    }

    /**
     * Executes an order step with the item supplier, adding the quantity
     * ordered to the given items.
     *
     * @param step
     *            - the order step to be executed by this item supplier.
     * @throws OrderProcessingException
     *             - if the step is malformed or another exception occurs (you
     *             may specialize exceptions deriving from
     *             OrderProcessingException if you want).
     */
    public synchronized void executeStep(OrderStep step)
          throws OrderProcessingException {
        if (step.getSupplierId() != this.supplierId) {
            throw new OrderProcessingException("Invalid supplierId: "
                                               + step.getSupplierId());
        }

        for (ItemQuantity item : step.getItems()) {
            if (!itemQuantities.containsKey(item.getItemId())) {
                throw new OrderProcessingException("Invalid itemId: "
                                                   + item.getItemId());
            }
            if (item.getQuantity() <= 0) {
                throw new OrderProcessingException("Invalid quantity: "
                                                   + item.getQuantity());
            }
        }

        try {
            logger.log(step);
        } catch (LogException e) {
            e.getException().printStackTrace();
            throw new OrderProcessingException("Logging failed!");
        }

        for (ItemQuantity item : step.getItems()) {
            itemQuantities.put(item.getItemId(),
                               itemQuantities.get(item.getItemId())
                                + item.getQuantity());
        }
    }

    /**
     * Returns the total quantity ordered per item at this item supplier.
     *
     * @param itemIds
     *            - the IDs of the items queried.
     * @return the position of the items.
     * @throws InvalidItemException
     *             - if any of the item IDs is unknown to this item supplier.
     */
    public synchronized List<ItemQuantity> getOrdersPerItem(Set<Integer> itemIds)
        throws InvalidItemException {
        ArrayList<ItemQuantity> result = new ArrayList<ItemQuantity>();

        for (Integer itemId : itemIds) {
            if (!itemQuantities.containsKey(itemId)) {
                throw new InvalidItemException("ItemId unknown: " + itemId);
            }

            result.add(new ItemQuantity(itemId, itemQuantities.get(itemId)));
        }

        return result;
    }

}
