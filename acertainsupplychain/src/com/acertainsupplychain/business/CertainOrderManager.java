package com.acertainsupplychain.business;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.Thread;
import java.lang.Runnable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;

import java.io.BufferedReader;
import java.io.FileReader;

import com.acertainsupplychain.interfaces.OrderManager;
import com.acertainsupplychain.interfaces.ItemSupplier;
import com.acertainsupplychain.client.ItemSupplierProxy;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.InvalidWorkflowException;
import com.acertainsupplychain.utils.Logger;
import com.acertainsupplychain.utils.LogException;
import com.acertainsupplychain.utils.SupplyChainUtility;

/**
 * The CertainOrderManager class is the basic implementation of the
 * OrderManager interface. It allows clients to submit and track order
 * workflows, which are recorded durably.
 */
public class CertainOrderManager implements OrderManager {

    private final int managerId;

    private final Logger logger;

    private Map<Integer, Workflow> workflows;
    private Map<Integer, ItemSupplier> itemSuppliers;;

    private final ThreadFactory threadFactory;

    private int workflowId = 0;

    public CertainOrderManager(int managerId, Map<Integer, ItemSupplier> itemSuppliers)
      throws LogException  {
        this.managerId = managerId;
        this.itemSuppliers = itemSuppliers;

        logger = new Logger("OrderManager" + managerId + ".log");

        workflows = new HashMap<Integer, Workflow>();

        int numConcurrentWorkerThreads = 10;
        threadFactory = Executors.defaultThreadFactory();

    }

    private class Worker implements Runnable {

        private Workflow workflow;

        public Worker(Workflow workflow, Map<Integer, ItemSupplier> itemSuppliers) {
            this.workflow = workflow;
        }

        public void run() {
            List<OrderStep> steps = workflow.getSteps();
            for (int i=0; i<steps.size(); i++) {
                OrderStep step = steps.get(i);
                int supplierId = step.getSupplierId();
                if (!itemSuppliers.containsKey(supplierId)) {
                    System.out.println("itemSuppliers indeholder ikke nøglen: " + supplierId);
                    System.out.println(itemSuppliers);
                    workflow.updateStatus(i, StepStatus.FAILED);
                    break;
                }

                ItemSupplier itemSupplier = itemSuppliers.get(supplierId);
                try {
                    itemSupplier.executeStep(step);
                } catch (OrderProcessingException e) {
                    System.out.println("OrderProcessingException");
                    e.printStackTrace();
                    workflow.updateStatus(i, StepStatus.FAILED);
                    break;
                }

                workflow.updateStatus(i, StepStatus.SUCCESSFUL);
            }
        }

    }

    /**
     * Registers an order workflow with the order manager.
     *
     * @param steps
     *            - the order steps to be executed.
     * @return the ID of the order workflow.
     * @throws OrderProcessingException
     *             - an exception thrown if steps are malformed or another error
     *             condition occurs (you may specialize exceptions deriving from
     *             OrderProcessingException if you want).
     */
    public synchronized int registerOrderWorkflow(List<OrderStep> steps)
      throws OrderProcessingException {
        assert(!workflows.containsKey(workflowId));

        assert itemSuppliers != null;

        Workflow workflow = new Workflow(workflowId, steps);
        Worker worker = new Worker(workflow, itemSuppliers);

        try {
            logger.log(steps);
        } catch (LogException e) {
            e.getException().printStackTrace();
            throw new OrderProcessingException("Logging failed!");
        }

        threadFactory.newThread(worker).start();

        workflows.put(workflowId, workflow);

        return workflowId++;

    }

    /**
     * Queries the current state of a given order workflow registered with the
     * order manager.
     *
     * @param orderWorkflowId
     *            - the ID of the workflow being queried.
     * @return the list of states of the multiple steps of the given workflow
     *         (order matters).
     * @throw InvalidWorkflowException - if the workflow ID given is not valid.
     */
    public List<StepStatus> getOrderWorkflowStatus(int orderWorkflowId)
      throws InvalidWorkflowException {
        if (workflows.containsKey(orderWorkflowId)) {
            return workflows.get(orderWorkflowId).getStatus();
        } else {
            throw new InvalidWorkflowException("The order workflow id does not exist: "
                                               + orderWorkflowId);
        }
    }

    public static void main(String[] args) {
        try {

            // First a test that works.
            Map<Integer, ItemSupplier> supplierMap = new HashMap<Integer, ItemSupplier>();
            ItemSupplier supplier = new ItemSupplierProxy("http://localhost:8081", 1);
            supplierMap.put(1, supplier);
            OrderManager orderManager = new CertainOrderManager(1, supplierMap);

            List<OrderStep> steps = new ArrayList<OrderStep>();
            for (int i=0; i<5; i++) {
                List<ItemQuantity> items = new ArrayList<ItemQuantity>();
                items.add(new ItemQuantity(i, i+10));
                steps.add(new OrderStep(1, items));
            }

            int workflowId = orderManager.registerOrderWorkflow(steps);
            System.out.println("Got workflowId: " + workflowId);

            System.out.println("Workflow status: "
                               + orderManager.getOrderWorkflowStatus(workflowId));

            Thread.sleep(4000);

            System.out.println("Workflow status: "
                               + orderManager.getOrderWorkflowStatus(workflowId));


            // Now let's try where one of the items has an invalid item id.
            steps = new ArrayList<OrderStep>();
            for (int i=0; i<5; i++) {
                List<ItemQuantity> items = new ArrayList<ItemQuantity>();
                items.add(new ItemQuantity(i+1, i+10));
                steps.add(new OrderStep(1, items));
            }

            workflowId = orderManager.registerOrderWorkflow(steps);
            System.out.println("Got workflowId: " + workflowId);

            System.out.println("Workflow status: "
                               + orderManager.getOrderWorkflowStatus(workflowId));

            Thread.sleep(4000);

            System.out.println("Workflow status: "
                               + orderManager.getOrderWorkflowStatus(workflowId));

            BufferedReader in
                = new BufferedReader(new FileReader("logs/OrderManager1.log"));
            String xml = in.readLine();
            System.out.println(xml);
            System.out.println(SupplyChainUtility.deserializeObject(xml));

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
