package com.acertainsupplychain.utils;

import java.util.List;

import com.acertainsupplychain.interfaces.OrderManager.StepStatus;

public class OrderManagerResponse {

    public enum OrderManagerResponseType { OK, FAIL };
    public OrderManagerResponseType type;

    public int workflowId;
    public List<StepStatus> orderWorkflowStatus;

}
