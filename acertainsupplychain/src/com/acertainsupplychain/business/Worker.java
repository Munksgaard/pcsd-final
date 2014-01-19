package com.acertainsupplychain.business;

import java.util.List;
import java.util.Map;
import java.util.LinkedList;

import com.acertainsupplychain.interfaces.ItemSupplier;
import com.acertainsupplychain.interfaces.OrderManager.StepStatus;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.CommunicationException;
import com.acertainsupplychain.utils.LogException;

/**
 * Class used for asynchronously executing workflows.
 */
public class Worker implements Runnable {

    private Workflow workflow;

    private Map<Integer, ItemSupplier> itemSuppliers;

    public Worker(Workflow workflow, Map<Integer, ItemSupplier> itemSuppliers) {
        this.workflow = workflow;
        this.itemSuppliers = itemSuppliers;
    }

    private class Helper {
        public int index;
        public OrderStep step;

        public Helper(int index, OrderStep step) {
            this.index = index;
            this.step = step;
        }
    }

    public void run() {
        List<OrderStep> steps = workflow.getSteps();
        LinkedList<Helper> queue = new LinkedList<Helper>();

        for (int i=0; i<steps.size(); i++) {
            OrderStep step = steps.get(i);
            int supplierId = step.getSupplierId();

            ItemSupplier itemSupplier = itemSuppliers.get(supplierId);
            try {
                itemSupplier.executeStep(step);

                workflow.updateStatus(i, StepStatus.SUCCESSFUL);
            } catch (LogException e) {
                // This is assumed to be recoverable
                queue.add(new Helper(i, steps.get(i)));
            } catch (CommunicationException e) {
                // This is assumed to be recovereable
                queue.add(new Helper(i, steps.get(i)));
            } catch (OrderProcessingException e) {
                // Here, there are 4 possibilities: Invalid item id,
                // invalid quantity, invalid supplier id, or the
                // request itself was malformed. All are unrecoverable
                workflow.updateStatus(i, StepStatus.FAILED);
            }
        }

        while (!queue.isEmpty()) {
            Helper help = queue.remove();
            OrderStep step = help.step;
            int supplierId = step.getSupplierId();
            ItemSupplier itemSupplier = itemSuppliers.get(supplierId);
            try {
                itemSupplier.executeStep(step);
                workflow.updateStatus(help.index, StepStatus.SUCCESSFUL);
            } catch (OrderProcessingException e) {
                // Since we already tested for unrecoverable failures,
                // we can safely add this back into the queue.
                queue.add(help);
            }
        }
    }

}
