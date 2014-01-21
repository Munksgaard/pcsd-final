package com.acertainsupplychain.tests;

import java.util.*;
import java.lang.*;
import java.util.concurrent.*;

import com.acertainsupplychain.business.*;
import com.acertainsupplychain.interfaces.OrderManager.StepStatus;
import com.acertainsupplychain.server.*;
import com.acertainsupplychain.client.*;
import com.acertainsupplychain.interfaces.*;
import com.acertainsupplychain.utils.*;

public class OrderManagerTest {

    public static void main(String[] args) {
        try {
            System.out.println("Starting OrderManagerTest...");
            System.out.println("You need to start the ItemSupplierHTTPServer on port 8080, id 0, and item IDs 0, 1, 2, 3, 4. and the OrderManagerHTTPServer on port 8000 with id 0\n");

            System.out.print("First a test to see that simple functionality works... ");

            Map<Integer, ItemSupplier> supplierMap = new HashMap<Integer, ItemSupplier>();
            ItemSupplier supplier = new ItemSupplierProxy("http://localhost:8080", 0);
            supplierMap.put(0, supplier);
            OrderManager orderManager
                = new OrderManagerProxy("http://localhost:8000");
            List<OrderStep> steps = new ArrayList<OrderStep>();
            for (int i=0; i<5; i++) {
                List<ItemQuantity> items = new ArrayList<ItemQuantity>();
                items.add(new ItemQuantity(i, i+10));
                steps.add(new OrderStep(0, items));
            }

            int workflowId = orderManager.registerOrderWorkflow(steps);

            Thread.sleep(5000);

            List<StepStatus> statuses = orderManager.getOrderWorkflowStatus(workflowId);
            for (StepStatus status : statuses) {
                assert status == StepStatus.SUCCESSFUL;
            }

            System.out.println("Success!");

            System.out.print("Invalid IDs should fail... ");
            steps = new ArrayList<OrderStep>();
            for (int i=0; i<5; i++) {
                List<ItemQuantity> items = new ArrayList<ItemQuantity>();
                items.add(new ItemQuantity(i-1, i+10));
                steps.add(new OrderStep(0, items));
            }

            workflowId = orderManager.registerOrderWorkflow(steps);

            Thread.sleep(2000);

            statuses = orderManager.getOrderWorkflowStatus(workflowId);

            for (int i=1; i<5; i++) {
                assert statuses.get(i) == StepStatus.SUCCESSFUL;
            }
            assert statuses.get(0) == StepStatus.FAILED;
            System.out.println("Success!");

            System.out.print("Now let's try an invalid supplier ID... ");

            List<ItemQuantity> tmpitems = new ArrayList<ItemQuantity>();
            tmpitems.add(new ItemQuantity(0, 10));
            steps.add(new OrderStep(1, tmpitems));

            boolean flag = false;
            try {
                orderManager.registerOrderWorkflow(steps);
            } catch (InvalidSupplierException e) {
                flag = true;
            }
            assert flag;
            System.out.println("Success!");

            System.out.print("Let's try an invalid workflow id... ");
            flag = false;
            try {
                orderManager.getOrderWorkflowStatus(-1);
            } catch (InvalidWorkflowException e) {
                flag = true;
            }
            assert flag;
            System.out.println("Success!");

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
