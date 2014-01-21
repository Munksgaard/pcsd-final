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

public class ItemServer {

    public static void main(String[] args) {
        try {
            System.out.println("Starting item server for performance testing...");

            Set<Integer> ids = new HashSet<Integer>();
            for (int i=0; i<100; i++) {
                ids.add(i);
            }

            ItemSupplierHTTPServer.startServer(8080, 0, ids);

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
