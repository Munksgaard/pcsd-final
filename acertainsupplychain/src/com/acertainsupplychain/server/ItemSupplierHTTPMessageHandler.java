package com.acertainsupplychain.server;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.acertainsupplychain.interfaces.ItemSupplier;
import com.acertainsupplychain.utils.ItemSupplierResponse;
import com.acertainsupplychain.utils.ItemSupplierResponse.ItemSupplierResponseType;
import com.acertainsupplychain.utils.ItemSupplierRequest;
import com.acertainsupplychain.utils.SupplyChainUtility;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.InvalidItemException;
import com.acertainsupplychain.utils.InvalidSupplierException;
import com.acertainsupplychain.utils.InvalidQuantityException;
import com.acertainsupplychain.utils.LogException;

public class ItemSupplierHTTPMessageHandler extends AbstractHandler {

    public static final String SERVICE_PREFIX = "/itemsupplier";

    protected ItemSupplier itemSupplier;

    public ItemSupplierHTTPMessageHandler(ItemSupplier itemSupplier) {
        assert itemSupplier != null;
        this.itemSupplier = itemSupplier;
    }

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) throws IOException, ServletException {

        httpResponse.setContentType("text/html;charset=utf-8");
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        String requestURI = httpRequest.getRequestURI();

        ItemSupplierResponse response = new ItemSupplierResponse();

        if (requestURI != null && requestURI.toLowerCase().equals(SERVICE_PREFIX)) {
            try {
                ItemSupplierRequest req =
                    (ItemSupplierRequest) SupplyChainUtility.extractRequest(httpRequest);
                switch (req.type) {
                case EXECUTE_STEP:
                    itemSupplier.executeStep(req.step);
                    response.type = ItemSupplierResponseType.OK;
                    break;

                case GET_ORDERS_PER_ITEM:
                    response.ordersPerItem = itemSupplier.getOrdersPerItem(req.itemIds);
                    response.type = ItemSupplierResponseType.OK;
                    break;

                }
            } catch (InvalidItemException e) {
                response.type = ItemSupplierResponseType.INVALID_ITEM;
            } catch (InvalidSupplierException e) {
                response.type = ItemSupplierResponseType.INVALID_SUPPLIER;
            } catch (InvalidQuantityException e) {
                response.type = ItemSupplierResponseType.INVALID_QUANTITY;
            } catch (LogException e) {
                response.type = ItemSupplierResponseType.LOG_EXCEPTION;
            } catch (OrderProcessingException e) {
                response.type = ItemSupplierResponseType.FAIL;
            } catch (Exception e) {
                response.type = ItemSupplierResponseType.FAIL;
            }

        } else {
            response.type = ItemSupplierResponseType.FAIL;
        }

        String xml = SupplyChainUtility.serializeObject(response);
        httpResponse.getWriter().println(xml);

        baseRequest.setHandled(true);
    }

}
