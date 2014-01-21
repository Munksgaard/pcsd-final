package com.acertainsupplychain.tests;

import java.util.*;
import java.lang.*;
import java.util.concurrent.*;

import com.acertainsupplychain.business.*;
import com.acertainsupplychain.interfaces.*;
import com.acertainsupplychain.utils.*;

public class ItemSupplierTest {

    public static void main(String[] args) {
        try {
            System.out.println("Starting ItemSupplierTest...\n");
            HashSet<Integer> itemIds = new HashSet<Integer>();
            itemIds.add(0);
            itemIds.add(1);
            itemIds.add(2);

            System.out.print("Item IDs should have 0 orders to begin with... ");
            final ItemSupplier supplier = new CertainItemSupplier(0, itemIds);

            List<ItemQuantity> result = supplier.getOrdersPerItem(itemIds);

            assert result.size() == 3;
            for (ItemQuantity item : result) {
                assert itemIds.contains(item.getItemId());
                assert item.getQuantity() == 0;

            }
            System.out.println("Success!");

            System.out.print("Getting an invalid item ID should trigger an exception... ");
            HashSet<Integer> itemIds2 = new HashSet<Integer>();
            itemIds2.add(0); // One good item ID
            itemIds2.add(3); // Bad!

            boolean flag = false;
            try {
                supplier.getOrdersPerItem(itemIds2);
            } catch (InvalidItemException e) {
                flag = true;
            }
            assert(flag);
            System.out.println("Success!");

            System.out.print("Let's try adding some orders... ");
            ArrayList<ItemQuantity> items = new ArrayList<ItemQuantity>();
            items.add(new ItemQuantity(0, 5));
            items.add(new ItemQuantity(1, 10));
            supplier.executeStep(new OrderStep(0, items));

            result = supplier.getOrdersPerItem(itemIds);
            assert result.size() == 3;
            for (ItemQuantity item : result) {
                if (item.getItemId() == 0) assert item.getQuantity() == 5;
                else if (item.getItemId() == 1) assert item.getQuantity() == 10;
                else if (item.getItemId() == 2) assert item.getQuantity() == 0;
                else assert false;
            }
            System.out.println("Success!");

            System.out.print("Let's try adding some more orders... ");
            items = new ArrayList<ItemQuantity>();
            items.add(new ItemQuantity(0, 5));
            items.add(new ItemQuantity(2, 1));
            supplier.executeStep(new OrderStep(0, items));

            result = supplier.getOrdersPerItem(itemIds);
            assert result.size() == 3;
            for (ItemQuantity item : result) {
                if (item.getItemId() == 0) assert item.getQuantity() == 10;
                else if (item.getItemId() == 1) assert item.getQuantity() == 10;
                else if (item.getItemId() == 2) assert item.getQuantity() == 1;
                else assert false;
            }
            System.out.println("Success!");

            System.out.print("Testing with an invalid supplier ID... ");
            flag = false;
            try {
                supplier.executeStep(new OrderStep(1, items));
            } catch (InvalidSupplierException e) {
                flag = true;
            }
            assert(flag);
            System.out.println("Success!");

            System.out.print("Testing atomicity with an invalid item ID...");
            items = new ArrayList<ItemQuantity>();
            items.add(new ItemQuantity(0, 5));
            items.add(new ItemQuantity(2, 4));
            items.add(new ItemQuantity(3, 1));

            flag = false;
            try {
                supplier.executeStep(new OrderStep(0, items));
            } catch (InvalidItemException e) {
                flag = true;
            }
            assert flag;

            result = supplier.getOrdersPerItem(itemIds);
            assert result.size() == 3;
            for (ItemQuantity item : result) {
                if (item.getItemId() == 0) assert item.getQuantity() == 10;
                else if (item.getItemId() == 1) assert item.getQuantity() == 10;
                else if (item.getItemId() == 2) assert item.getQuantity() == 1;
                else assert false;
            }
            System.out.println("Success!");

            System.out.print("Testing atomicity with an invalid quantity...");
            items = new ArrayList<ItemQuantity>();
            items.add(new ItemQuantity(0, 5));
            items.add(new ItemQuantity(2, 4));
            items.add(new ItemQuantity(1, -1));

            flag = false;
            try {
                supplier.executeStep(new OrderStep(0, items));
            } catch (InvalidQuantityException e) {
                flag = true;
            }
            assert flag;

            result = supplier.getOrdersPerItem(itemIds);
            assert result.size() == 3;
            for (ItemQuantity item : result) {
                if (item.getItemId() == 0) assert item.getQuantity() == 10;
                else if (item.getItemId() == 1) assert item.getQuantity() == 10;
                else if (item.getItemId() == 2) assert item.getQuantity() == 1;
                else assert false;
            }
            System.out.println("Success!");

            System.out.print("Let's try spamming the server a bit, to check whether executeStep is atomic with regards to getOrdersPerItem... ");

            flag = true;

            Thread executor = new Thread() {
                    public void run() {
                        List<ItemQuantity> itemIds
                            = new ArrayList<ItemQuantity>();
                        for (int i=0; i<1000; i++) {
                            itemIds.clear();
                            itemIds.add(new ItemQuantity(0, 5));
                            itemIds.add(new ItemQuantity(1, 5));
                            try {
                                supplier.executeStep(new OrderStep(0, itemIds));
                            } catch (Exception e) {
                                assert false;
                            }
                        }
                    }
                };

            Thread reader = new Thread() {
                    public void run() {
                        Set<Integer> itemIds = new HashSet<Integer>();
                        List<ItemQuantity> items;
                        for (int i=0; i<1000; i++) {
                            itemIds.clear();
                            itemIds.add(0);
                            itemIds.add(1);
                            try {
                                items = supplier.getOrdersPerItem(itemIds);
                                assert items.size() == 2;
                                assert items.get(0).getQuantity()
                                    == items.get(1).getQuantity();
                                assert items.get(0).getQuantity() % 5 == 0;
                            } catch (Exception e) {
                                assert false;
                            }
                        }
                    }
                };

            executor.start();
            reader.start();

            executor.join();
            reader.join();

            assert flag;

            System.out.println("Success!");

            System.out.print("Now, let's try several executors at once, to test isolation... ");

            flag = true;


            Set<Integer> itemset = new HashSet<Integer>();
            itemset.add(0);

            List<ItemQuantity> results = supplier.getOrdersPerItem(itemset);
            int i0 = results.get(0).getQuantity();

            Thread executor1 = new Thread() {
                    public void run() {
                        List<ItemQuantity> itemquants
                            = new ArrayList<ItemQuantity>();
                        for (int i=0; i<1000; i++) {
                            itemquants.clear();
                            itemquants.add(new ItemQuantity(0, 5));
                            itemquants.add(new ItemQuantity(0, 5));
                            try {
                                supplier.executeStep(new OrderStep(0, itemquants));
                            } catch (Exception e) {
                                assert false;
                            }
                        }
                    }
                };

            Thread executor2 = new Thread() {
                    public void run() {
                        List<ItemQuantity> itemquants
                            = new ArrayList<ItemQuantity>();
                        for (int i=0; i<1000; i++) {
                            itemquants.clear();
                            itemquants.add(new ItemQuantity(0, 5));
                            itemquants.add(new ItemQuantity(0, 5));
                            try {
                                supplier.executeStep(new OrderStep(0, itemquants));
                            } catch (Exception e) {
                                assert false;
                            }
                        }
                    }
                };

            executor1.start();
            executor2.start();

            executor1.join();
            executor2.join();

            assert flag;

            results = supplier.getOrdersPerItem(itemset);
            assert results.get(0).getQuantity() == i0 + 5 * 4000;

            System.out.println("Success!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
