package com.acertainsupplychain.performance;

import java.util.*;
import java.lang.*;
import java.util.concurrent.*;

import com.acertainsupplychain.business.*;
import com.acertainsupplychain.interfaces.OrderManager.StepStatus;
import com.acertainsupplychain.server.*;
import com.acertainsupplychain.client.*;
import com.acertainsupplychain.interfaces.*;
import com.acertainsupplychain.utils.*;

public class OrderServer {

    public static void main(String[] args) {
        try {
            assert args.length == 1;
            System.out.println("Starting order server for performance testing...");

            Map<Integer, ItemSupplier> suppliers = new HashMap<Integer, ItemSupplier>();
            suppliers.put(0, new ItemSupplierProxy("http://localhost:8080", 0));
            int id = Integer.parseInt(args[0]);
            OrderManagerHTTPServer.startServer(8000 + id, id, suppliers);

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
