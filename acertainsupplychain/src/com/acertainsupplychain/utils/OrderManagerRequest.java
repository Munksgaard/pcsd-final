package com.acertainsupplychain.utils;

import java.util.List;

import com.acertainsupplychain.business.OrderStep;

public class OrderManagerRequest {

    public enum OrderManagerRequestType { REGISTER_ORDER_WORKFLOW, GET_ORDER_WORKFLOW_STATUS };
    public OrderManagerRequestType type;
    public List<OrderStep> steps;
    public int workflowId;

}
