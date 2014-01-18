package com.acertainsupplychain.server;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import com.acertainsupplychain.business.CertainOrderManager;
import com.acertainsupplychain.utils.HTTPServerUtility;
import com.acertainsupplychain.utils.SupplyChainUtility;
import com.acertainsupplychain.interfaces.OrderManager;
import com.acertainsupplychain.interfaces.ItemSupplier;
import com.acertainsupplychain.utils.LogException;
import com.acertainsupplychain.client.ItemSupplierProxy;

public class OrderManagerHTTPServer {

    /**
     * Run HTTP server using externally provided map of branches/accounts.
     *
     * This method blocks.
     */
    public static void startServer(int portNo, int managerId,
                                   Map<Integer, ItemSupplier> suppliers) {
        try {
            // Initialize order manager
            OrderManager orderManager = new CertainOrderManager(managerId, suppliers);

            // Initialize message handler
            OrderManagerHTTPMessageHandler handler =
                new OrderManagerHTTPMessageHandler(orderManager);

            // Create server
            if (!HTTPServerUtility.createServer(portNo, handler)) {
                System.out.println("Server: Failed.");
                System.exit(1);
            }
        } catch (LogException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        // Parse command-line arguments
        if (args.length < 3) {
            System.out.println("Usage: <port> <id> <supplier1> ... <supplierN>");
            System.exit(1);
        }

        // Start server if possible
        try {
            Map<Integer, ItemSupplier> suppliers = new HashMap<Integer, ItemSupplier>();
            for (int i=2; i<args.length; i++) {
                suppliers.put(i-2, new ItemSupplierProxy(args[i], i-2));
            }
            startServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), suppliers);
        } catch (NumberFormatException e) {
            System.out.println("OrderManagerHTTPServer: Invalid input.");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
