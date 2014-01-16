package com.acertainsupplychain.business;

import java.util.List;
import java.util.ArrayList;

import com.acertainsupplychain.interfaces.OrderManager.StepStatus;

public class Workflow {

    private final int id;
    private List<OrderStep> steps;
    private ArrayList<StepStatus> statuses;

    public Workflow(int id, List<OrderStep> steps) {
        this.id = id;
        this.steps = steps;
        statuses = new ArrayList<StepStatus>(steps.size());
        for (int i=0; i<steps.size(); i++) {
            statuses.add(StepStatus.REGISTERED);
        }
    }

    public int getId() {
        return id;
    }

    public List<OrderStep> getSteps() {
        return steps;
    }

    public List<StepStatus> getStatus() {
        return statuses;
    }

    public void updateStatus(int stepId, StepStatus status)
      throws IndexOutOfBoundsException {
        statuses.set(stepId, status);
    }
}
