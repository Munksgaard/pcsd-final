package com.acertainsupplychain.server;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.acertainsupplychain.interfaces.OrderManager;
import com.acertainsupplychain.utils.SupplyChainUtility;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.OrderManagerRequest;
import com.acertainsupplychain.utils.OrderManagerResponse;
import com.acertainsupplychain.utils.OrderManagerResponse.OrderManagerResponseType;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.InvalidItemException;
import com.acertainsupplychain.utils.InvalidSupplierException;
import com.acertainsupplychain.utils.InvalidQuantityException;
import com.acertainsupplychain.utils.InvalidWorkflowException;
import com.acertainsupplychain.utils.LogException;


public class OrderManagerHTTPMessageHandler extends AbstractHandler {

    public static final String SERVICE_PREFIX = "/ordermanager";

    protected OrderManager orderManager;

    public OrderManagerHTTPMessageHandler(OrderManager orderManager) {
        assert orderManager != null;
        this.orderManager = orderManager;
    }

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) throws IOException, ServletException {

        httpResponse.setContentType("text/html;charset=utf-8");
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        String requestURI = httpRequest.getRequestURI();

        OrderManagerResponse response = new OrderManagerResponse();

        if (requestURI != null && requestURI.toLowerCase().equals(SERVICE_PREFIX)) {
            OrderManagerRequest req =
                (OrderManagerRequest) SupplyChainUtility.extractRequest(httpRequest);

            try {
                switch (req.type) {
                case REGISTER_ORDER_WORKFLOW:
                    response.workflowId = orderManager.registerOrderWorkflow(req.steps);
                    response.type = OrderManagerResponseType.OK;
                    break;

                case GET_ORDER_WORKFLOW_STATUS:
                    response.orderWorkflowStatus = orderManager.getOrderWorkflowStatus(req.workflowId);
                    response.type = OrderManagerResponseType.OK;
                    break;

                }
            } catch (InvalidItemException e) {
                response.type = OrderManagerResponseType.INVALID_ITEM;
            } catch (InvalidSupplierException e) {
                response.type = OrderManagerResponseType.INVALID_SUPPLIER;
            } catch (InvalidQuantityException e) {
                response.type = OrderManagerResponseType.INVALID_QUANTITY;
            } catch (LogException e) {
                response.type = OrderManagerResponseType.LOG_EXCEPTION;
            } catch (InvalidWorkflowException e) {
                response.type = OrderManagerResponseType.INVALID_WORKFLOW;
            } catch (OrderProcessingException e) {
                response.type = OrderManagerResponseType.FAIL;
            }

        } else {
            response.type = OrderManagerResponseType.FAIL;
        }

        String xml = SupplyChainUtility.serializeObject(response);
        httpResponse.getWriter().println(xml);

        baseRequest.setHandled(true);
    }

}
