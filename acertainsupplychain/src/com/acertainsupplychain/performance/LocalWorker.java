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

public class LocalWorker implements Callable<RunResult> {
    private ItemSupplier supplier;
    private int numSuccess = 0;
    private int totalRuns = 0;

    private int warmupRuns;
    private int realRuns;
    private Random rand;

    public LocalWorker(ItemSupplier supplier, int warmupRuns, int realRuns) {
        this.supplier = supplier;
        this.warmupRuns = warmupRuns;
        this.realRuns = realRuns;
        rand = new Random();
    }

    private boolean getOrders() {
        try {
            ArrayList<Integer> items = new ArrayList<Integer>(100);
            for (int i=0; i<rand.nextInt(100); i++) {
                items.add(rand.nextInt(100));
            }

            supplier.getOrdersPerItem(new HashSet<Integer>(items));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean execStep() {
        try {
            ArrayList<ItemQuantity> items = new ArrayList<ItemQuantity>();

            for (int i=0; i<rand.nextInt(20); i++) {
                items.add(new ItemQuantity(rand.nextInt(100), 2));
            }

            supplier.executeStep(new OrderStep(0, items));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public RunResult call() throws Exception {
        int count = 1;
        long startTimeNS = 0;
        long endTimeNS = 0;
        int successes = 0;
        long delta = 0;

        float ratio = 0.1f;

        // Perform the warmup runs
        while (count++ <= warmupRuns) {
            if (rand.nextFloat() > ratio) {
                execStep();
            } else {
                getOrders();
            }
        }

        count = 1;
        // Perform the actual runs
        startTimeNS = System.nanoTime();
        while (count++ <= realRuns) {
            if (rand.nextFloat() > ratio) {
                if (execStep()) successes++;
            } else {
                if (getOrders()) successes++;
            }
        }
        endTimeNS = System.nanoTime();
        delta = endTimeNS - startTimeNS;

        return new RunResult(successes, realRuns, delta);
    }

}
