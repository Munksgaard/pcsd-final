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

public class RemoteWorker implements Callable<RunResult> {
    private OrderManager orderManager;
    private int numSuccess = 0;
    private int totalRuns = 0;

    private int warmupRuns;
    private int realRuns;
    private Random rand;

    private ArrayList<Integer> workflowIds;

    public RemoteWorker(OrderManager orderManager, int warmupRuns, int realRuns) {
        this.orderManager = orderManager;
        this.warmupRuns = warmupRuns;
        this.realRuns = realRuns;
        workflowIds = new ArrayList<Integer>();
        rand = new Random();
    }

    private boolean registerOrders() {
        try {
            ArrayList<OrderStep> steps = new ArrayList<OrderStep>();
            for (int i=0; i<rand.nextInt(10); i++) {
                ArrayList<ItemQuantity> items = new ArrayList<ItemQuantity>();
                for (int j=0; j<rand.nextInt(100); i++) {
                    items.add(new ItemQuantity(rand.nextInt(100), 2));
                }
                steps.add(new OrderStep(0, items));
            }

            workflowIds.add(orderManager.registerOrderWorkflow(steps));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean getStatus() {
        if (workflowIds.size() > 0) {
            try {
                int id = workflowIds.get(rand.nextInt(workflowIds.size()));
                orderManager.getOrderWorkflowStatus(id);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public RunResult call() throws Exception {
        int count = 1;
        long startTimeNS = 0;
        long endTimeNS = 0;
        int successes = 0;
        long delta = 0;

        float ratio = 0.4f;

        // Perform the warmup runs
        while (count++ <= warmupRuns) {
            if (rand.nextFloat() > ratio) {
                getStatus();
            } else {
                registerOrders();
            }
        }

        count = 1;
        // Perform the actual runs
        startTimeNS = System.nanoTime();
        while (count++ <= realRuns) {
            if (rand.nextFloat() > ratio) {
                if (getStatus()) successes++;
            } else {
                if (registerOrders()) successes++;
            }
        }
        endTimeNS = System.nanoTime();
        delta = endTimeNS - startTimeNS;

        return new RunResult(successes, realRuns, delta);
    }

}
