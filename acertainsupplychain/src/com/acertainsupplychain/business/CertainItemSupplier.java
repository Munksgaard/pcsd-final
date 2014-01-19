package com.acertainsupplychain.business;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import java.io.BufferedReader;
import java.io.FileReader;

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

    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    /**
     * Initializes the item supplier with the given set of itemIds
     *
     * @param supplierId
     *  - The supplier ID for this particular supplier
     *
     * @param itemIds
     *  - The set of valid item IDs that the supplier supplies
     *
     * @throws LogException
     *  - if creating the log fails
     */
    public CertainItemSupplier(int supplierId, Set<Integer> itemIds)
      throws LogException {
        this.supplierId = supplierId;

        this.logger = new Logger("ItemSupplier" + supplierId + ".log");

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
    public void executeStep(OrderStep step)
      throws OrderProcessingException {
        if (step.getSupplierId() != this.supplierId) {
            throw new OrderProcessingException("Invalid supplierId: "
                                               + step.getSupplierId());
        }

        for (ItemQuantity item : step.getItems()) {
            if (!itemQuantities.containsKey(item.getItemId())) {
                throw new InvalidItemException("Invalid itemId: "
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

        rwl.writeLock().lock();
        try {
            for (ItemQuantity item : step.getItems()) {
                itemQuantities.put(item.getItemId(),
                                   itemQuantities.get(item.getItemId())
                                   + item.getQuantity());
            }
        } finally {
            rwl.writeLock().unlock();
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
    public List<ItemQuantity> getOrdersPerItem(Set<Integer> itemIds)
      throws InvalidItemException {
        ArrayList<ItemQuantity> result = new ArrayList<ItemQuantity>();


        for (Integer itemId : itemIds) {
            if (!itemQuantities.containsKey(itemId)) {
                throw new InvalidItemException("ItemId unknown: " + itemId);
            }
        }

        rwl.readLock().lock();
        try {
            for (Integer itemId : itemIds) {
                result.add(new ItemQuantity(itemId, itemQuantities.get(itemId)));
            }
        } finally {
            rwl.readLock().unlock();
        }

        return result;
    }

    public static void main(String[] args) {
        try {
            HashSet<Integer> itemIds = new HashSet<Integer>();
            itemIds.add(42);
            itemIds.add(43);
            itemIds.add(2);

            // Test constructor
            CertainItemSupplier supplier = new CertainItemSupplier(1, itemIds);

            List<ItemQuantity> result = supplier.getOrdersPerItem(itemIds);
            System.out.println("Should print three empty itemQuantities: 42, 43, and 2");
            for (ItemQuantity item : result) {
                System.out.println(item);
            }

            // Test getOrdersPerItemId with invalid itemId
            HashSet<Integer> itemIds2 = new HashSet<Integer>();
            itemIds2.add(43);
            itemIds2.add(3);

            boolean flag = false;
            try {
                supplier.getOrdersPerItem(itemIds2);
            } catch (InvalidItemException e) {
                flag = true;
            }
            assert(flag);

            ArrayList<ItemQuantity> items = new ArrayList<ItemQuantity>();
            items.add(new ItemQuantity(42, 3));
            items.add(new ItemQuantity(43, 7));
            supplier.executeStep(new OrderStep(1, items));

            result = supplier.getOrdersPerItem(itemIds);
            System.out.println("Should print three itemQuantities: 42, 43, and 2. Now 42 and 43 should have some quantities");
            for (ItemQuantity item : result) {
                System.out.println(item);
            }

            items = new ArrayList<ItemQuantity>();
            items.add(new ItemQuantity(42, 3));
            items.add(new ItemQuantity(43, 7));
            supplier.executeStep(new OrderStep(1, items));

            result = supplier.getOrdersPerItem(itemIds);
            System.out.println("Should print three itemQuantities: 42, 43, and 2. Now 42 and 43 should have bigger quantities");
            for (ItemQuantity item : result) {
                System.out.println(item);
            }

            flag = false;
            try {
                supplier.executeStep(new OrderStep(0, items));
            } catch (OrderProcessingException e) {
                flag = true;
            }
            assert(flag);

            BufferedReader in
                = new BufferedReader(new FileReader("logs/ItemSupplier1.log"));
            String xml = in.readLine();
            System.out.println(xml);
            System.out.println(SupplyChainUtility.deserializeObject(xml));

            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
