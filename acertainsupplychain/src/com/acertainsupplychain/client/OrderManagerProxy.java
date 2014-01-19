package com.acertainsupplychain.client;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Properties;
import java.io.FileInputStream;
import java.lang.Thread;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.interfaces.OrderManager;
import com.acertainsupplychain.utils.OrderManagerResponse;
import com.acertainsupplychain.utils.OrderManagerResponse.OrderManagerResponseType;
import com.acertainsupplychain.utils.OrderManagerRequest;
import com.acertainsupplychain.utils.OrderManagerRequest.OrderManagerRequestType;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.InvalidItemException;
import com.acertainsupplychain.utils.SupplyChainConstants;
import com.acertainsupplychain.utils.SupplyChainUtility;

public class OrderManagerProxy implements OrderManager {

    private HttpClient client;
    private final String server;

    public OrderManagerProxy(String server) throws Exception {
        client = new HttpClient();
        client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        client.setMaxConnectionsPerAddress(OrderManagerClientConstants.CLIENT_MAX_CONNECTION_ADDRESS);
        client.setThreadPool(new QueuedThreadPool(OrderManagerClientConstants.CLIENT_MAX_THREADSPOOL_THREADS));
        client.setTimeout(OrderManagerClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS);
        client.start();

        this.server = server;
    }

    public int registerOrderWorkflow(List<OrderStep> steps)
      throws OrderProcessingException {
        OrderManagerRequest req = new OrderManagerRequest();
        req.type = OrderManagerRequestType.REGISTER_ORDER_WORKFLOW;
        req.steps = steps;

        String xml = SupplyChainUtility.serializeObject(req);

        Buffer requestContent = new ByteArrayBuffer(xml);

        ContentExchange exchange = new ContentExchange();

        exchange.setMethod("POST");
        exchange.setURL(server + "/ordermanager");
        exchange.setRequestContent(requestContent);

        try {
            OrderManagerResponse response =
                (OrderManagerResponse) SupplyChainUtility.SendAndRecv(this.client, exchange);
            if (response.type==OrderManagerResponseType.FAIL) {
                throw new OrderProcessingException("Registering order workflow failed.");
            }
            return response.workflowId;
        } catch (OrderProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderProcessingException("Communication failed.");
        }
    }

    public List<StepStatus> getOrderWorkflowStatus(int orderWorkflowId)
        throws InvalidItemException, OrderProcessingException {
        OrderManagerRequest req = new OrderManagerRequest();
        req.type = OrderManagerRequestType.GET_ORDER_WORKFLOW_STATUS;
        req.workflowId = orderWorkflowId;

        String xml = SupplyChainUtility.serializeObject(req);

        Buffer requestContent = new ByteArrayBuffer(xml);

        ContentExchange exchange = new ContentExchange();

        exchange.setMethod("POST");
        exchange.setURL(server + "/ordermanager");
        exchange.setRequestContent(requestContent);

        try {
            OrderManagerResponse response =
                (OrderManagerResponse) SupplyChainUtility.SendAndRecv(this.client, exchange);
            if (response.type==OrderManagerResponseType.FAIL) {
                throw new InvalidItemException("Getting order workflow status failed.");
            }
            return response.orderWorkflowStatus;
        } catch (OrderProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderProcessingException("Communication failed.");
        }
    }

    public void stop() throws Exception {
        client.stop();
    }

    public static void main(String args[]) {
        try {
            if (args.length != 1) {
                System.out.println("OrderManagerHTTPServer: no port specified.");
                System.exit(1);
            }

            OrderManager orderManager
                = new OrderManagerProxy("http://localhost:" + Integer.parseInt(args[0]));

            List<OrderStep> steps = new ArrayList<OrderStep>();
            for (int i=0; i<5; i++) {
                List<ItemQuantity> items = new ArrayList<ItemQuantity>();
                items.add(new ItemQuantity(i, i+10));
                steps.add(new OrderStep(0, items));
            }

            int workflowId = orderManager.registerOrderWorkflow(steps);
            System.out.println("Got workflowId: " + workflowId);

            System.out.println("Workflow status: "
                               + orderManager.getOrderWorkflowStatus(workflowId));

            Thread.sleep(4000);

            System.out.println("Workflow status: "
                               + orderManager.getOrderWorkflowStatus(workflowId));

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
