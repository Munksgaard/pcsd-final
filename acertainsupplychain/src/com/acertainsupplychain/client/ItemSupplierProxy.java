package com.acertainsupplychain.client;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.InterruptedException;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.interfaces.ItemSupplier;
import com.acertainsupplychain.utils.ItemSupplierResponse;
import com.acertainsupplychain.utils.ItemSupplierResponse.ItemSupplierResponseType;
import com.acertainsupplychain.utils.ItemSupplierRequest;
import com.acertainsupplychain.utils.ItemSupplierRequest.ItemSupplierRequestType;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.InvalidItemException;
import com.acertainsupplychain.utils.InvalidSupplierException;
import com.acertainsupplychain.utils.InvalidQuantityException;
import com.acertainsupplychain.utils.SupplyChainConstants;
import com.acertainsupplychain.utils.SupplyChainUtility;

public class ItemSupplierProxy implements ItemSupplier {

    private HttpClient client;
    private final String server;
    private final int supplierId;

    public ItemSupplierProxy(String server, int supplierId) throws Exception {
        client = new HttpClient();
        client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        client.setMaxConnectionsPerAddress(ItemSupplierClientConstants.CLIENT_MAX_CONNECTION_ADDRESS);
        client.setThreadPool(new QueuedThreadPool(ItemSupplierClientConstants.CLIENT_MAX_THREADSPOOL_THREADS));
        client.setTimeout(ItemSupplierClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS);
        client.start();

        this.server = server;
        this.supplierId = supplierId;
    }

    public void executeStep(OrderStep step) throws OrderProcessingException {
        ItemSupplierRequest req = new ItemSupplierRequest();
        req.type = ItemSupplierRequestType.EXECUTE_STEP;
        req.step = step;

        String xml = SupplyChainUtility.serializeObject(req);

        Buffer requestContent = new ByteArrayBuffer(xml);

        ContentExchange exchange = new ContentExchange();

        exchange.setMethod("POST");
        exchange.setURL(server + "/itemsupplier");
        exchange.setRequestContent(requestContent);

        try {
            ItemSupplierResponse response =
                (ItemSupplierResponse) SupplyChainUtility.SendAndRecv(this.client, exchange);
            if (response.type==ItemSupplierResponseType.FAIL) {
                throw new OrderProcessingException("Placing order failed.");
            }
        } catch (OrderProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderProcessingException("Communication failed.");
        }
    }

    public List<ItemQuantity> getOrdersPerItem(Set<Integer> itemIds)
      throws OrderProcessingException {
        ItemSupplierRequest req = new ItemSupplierRequest();
        req.type = ItemSupplierRequestType.GET_ORDERS_PER_ITEM;
        req.itemIds = itemIds;

        String xml = SupplyChainUtility.serializeObject(req);

        Buffer requestContent = new ByteArrayBuffer(xml);

        ContentExchange exchange = new ContentExchange();

        exchange.setMethod("POST");
        exchange.setURL(server + "/itemsupplier");
        exchange.setRequestContent(requestContent);

        try{
            ItemSupplierResponse response =
                (ItemSupplierResponse) SupplyChainUtility.SendAndRecv(this.client, exchange);
            if (response.type!=ItemSupplierResponseType.FAIL) {
                throw new OrderProcessingException("Getting orders failed.");
            } else if (response.type==ItemSupplierResponseType.INVALID_ITEM) {
                throw new InvalidItemException("Invalid item IDs.");
            } else if (response.type!=ItemSupplierResponseType.OK) {
                throw new OrderProcessingException("This shouldn't happen.");
            }

            return response.ordersPerItem;
        } catch (IOException e) {
            throw new OrderProcessingException("Communication failed");
        } catch (InterruptedException e) {
            throw new OrderProcessingException("Communication failed");
        }

    }

    public void stop() throws Exception {
        client.stop();
    }

    public static void main(String args[]) {
        try {
            ItemSupplierProxy supplier = new ItemSupplierProxy("http://localhost:8081", 1);

            HashSet<Integer> itemIds = new HashSet<Integer>();
            itemIds.add(42);
            itemIds.add(43);
            itemIds.add(2);

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

            flag = false;
            try {
                supplier.executeStep(new OrderStep(0, items));
            } catch (OrderProcessingException e) {
                flag = true;
            }
            assert(flag);

            supplier.stop();
            System.out.println("Done");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
