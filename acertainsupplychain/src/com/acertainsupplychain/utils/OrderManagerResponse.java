package com.acertainsupplychain.utils;

import java.util.List;

import com.acertainsupplychain.interfaces.OrderManager.StepStatus;

public class OrderManagerResponse {

    public enum OrderManagerResponseType { OK, FAIL, INVALID_WORKFLOW,
                                           INVALID_SUPPLIER, INVALID_ITEM,
                                           INVALID_QUANTITY, LOG_EXCEPTION };
    public OrderManagerResponseType type;

    public int workflowId;
    public List<StepStatus> orderWorkflowStatus;

}
