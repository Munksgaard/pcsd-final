package com.acertainsupplychain.server;

import java.util.Set;
import java.util.HashSet;

import com.acertainsupplychain.business.CertainItemSupplier;
import com.acertainsupplychain.utils.HTTPServerUtility;
import com.acertainsupplychain.utils.SupplyChainUtility;
import com.acertainsupplychain.interfaces.ItemSupplier;
import com.acertainsupplychain.utils.LogException;

public class ItemSupplierHTTPServer {

    /**
     * Run HTTP server using small predefined test set
     *
     * This method blocks.
     */
    public static void startServer(int portNo, int supplierId) {
        // Construct small test set
        HashSet<Integer> itemIds = new HashSet<Integer>();
        for (int i=0; i<5; i++) {
            itemIds.add(i);
        }

        // Start Server
        startServer(portNo, supplierId, itemIds);
    }

    /**
     * Run HTTP server using externally provided map of branches/accounts.
     *
     * This method blocks.
     */
    public static void startServer(int portNo, int supplierId, Set<Integer> itemIds) {
        try {
            // Initialize item supplier
            ItemSupplier itemSupplier =
                new CertainItemSupplier(supplierId, itemIds);

            // Initialize message handler
            ItemSupplierHTTPMessageHandler handler =
                new ItemSupplierHTTPMessageHandler(itemSupplier);

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
            System.out.println("Usage: <port> <supplierId> <itemId> ...");
            System.exit(1);
        }

        Set<Integer> itemIds = new HashSet<Integer>();
        for (int i=2; i<args.length; i++) {
            itemIds.add(Integer.parseInt(args[i]));
        }

        System.out.println(itemIds);

        // Start server if possible
        try {
            startServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), itemIds);
        } catch (NumberFormatException e) {
            System.out.println("Usage: <port> <id> <itemId> ...");
            System.exit(1);
        }
    }

}
