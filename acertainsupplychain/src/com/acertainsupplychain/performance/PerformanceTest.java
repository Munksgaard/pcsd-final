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

public class PerformanceTest {

    public static void reportMetric(List<RunResult> runResults) {
        double throughput = (double)0;
        double latency = (double)0;
        long totalTime = (long)0;
        int total = 0;
        int success = 0;
        int numWorkers = runResults.size();

        for (RunResult result : runResults) {
            total += result.totalRuns;
            success += result.successes;
            totalTime += result.deltaNS;

            throughput += (result.successes / (result.deltaNS / 10e6));
        }

        latency = ((double)totalTime / (double)total / 10e6);
        System.out.println("" + numWorkers + " " + success + " " + (total - success) + " " + throughput + " " + latency);
    }

    public static void main(String[] args) {
        try {
            int threadpoolsize = 20;
            int numlocalthreads = 5;
            int numremotethreads = numlocalthreads;
            int warmupruns = 200;
            int realruns = 500;
            System.out.println("Starting PerformanceTest...");

            ItemSupplier supplier = new ItemSupplierProxy("http://localhost:8080", 0);

            ExecutorService exec = Executors.newFixedThreadPool(threadpoolsize);

            List<RunResult> runResults = new ArrayList<RunResult>();
            List<Future<RunResult>> futureResults = new ArrayList<Future<RunResult>>();

            for (int i=0; i<numlocalthreads; i++) {
                LocalWorker localWorker = new LocalWorker(supplier, warmupruns, realruns);
                futureResults.add(exec.submit(localWorker));
            }

            for (int i=0; i<numremotethreads; i++) {
                String port = "";
                if (i > 9) {
                    port = "80" + i;
                } else {
                    port = "800" + i;
                }
                OrderManager orderManager = new OrderManagerProxy("http://localhost:"+ port);

                RemoteWorker remoteWorker = new RemoteWorker(orderManager, warmupruns, realruns);
                futureResults.add(exec.submit(remoteWorker));
            }

            for (Future<RunResult> futureResult : futureResults) {
                RunResult runResult = futureResult.get(); // blocking call
                runResults.add(runResult);
            }

            reportMetric(runResults);

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
