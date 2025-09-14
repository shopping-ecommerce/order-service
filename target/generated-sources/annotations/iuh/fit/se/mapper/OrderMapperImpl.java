package iuh.fit.se.mapper;

import iuh.fit.se.dto.request.OrderItemRequest;
import iuh.fit.se.dto.request.OrderRequest;
import iuh.fit.se.dto.response.OrderItemResponse;
import iuh.fit.se.dto.response.OrderResponse;
import iuh.fit.se.entity.Order;
import iuh.fit.se.entity.OrderItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderResponse toOrderResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.orderItems( orderItemListToOrderItemResponseList( order.getOrderItems() ) );
        orderResponse.id( order.getId() );
        orderResponse.userId( order.getUserId() );
        orderResponse.sellerId( order.getSellerId() );
        orderResponse.subtotal( order.getSubtotal() );
        orderResponse.discountAmount( order.getDiscountAmount() );
        orderResponse.shippingFee( order.getShippingFee() );
        orderResponse.totalAmount( order.getTotalAmount() );
        orderResponse.status( order.getStatus() );
        orderResponse.paymentStatus( order.getPaymentStatus() );
        orderResponse.shippingAddress( order.getShippingAddress() );
        orderResponse.phoneNumber( order.getPhoneNumber() );
        orderResponse.recipientName( order.getRecipientName() );
        orderResponse.notes( order.getNotes() );
        orderResponse.createdTime( order.getCreatedTime() );
        orderResponse.modifiedTime( order.getModifiedTime() );

        return orderResponse.build();
    }

    @Override
    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemResponse.OrderItemResponseBuilder orderItemResponse = OrderItemResponse.builder();

        orderItemResponse.productImage( orderItem.getProductImage() );
        orderItemResponse.productId( orderItem.getProductId() );
        orderItemResponse.productName( orderItem.getProductName() );
        orderItemResponse.quantity( orderItem.getQuantity() );
        orderItemResponse.size( orderItem.getSize() );
        orderItemResponse.unitPrice( orderItem.getUnitPrice() );
        orderItemResponse.totalPrice( orderItem.getTotalPrice() );

        return orderItemResponse.build();
    }

    @Override
    public Order toOrder(OrderRequest orderRequest) {
        if ( orderRequest == null ) {
            return null;
        }

        Order.OrderBuilder order = Order.builder();

        order.orderItems( orderItemRequestListToOrderItemList( orderRequest.getItems() ) );
        order.userId( orderRequest.getUserId() );
        order.sellerId( orderRequest.getSellerId() );
        order.shippingAddress( orderRequest.getShippingAddress() );
        order.phoneNumber( orderRequest.getPhoneNumber() );
        order.recipientName( orderRequest.getRecipientName() );
        order.notes( orderRequest.getNotes() );

        return order.build();
    }

    @Override
    public OrderItem toOrderItem(OrderItemRequest orderItemRequest) {
        if ( orderItemRequest == null ) {
            return null;
        }

        OrderItem.OrderItemBuilder orderItem = OrderItem.builder();

        orderItem.productId( orderItemRequest.getProductId() );
        orderItem.size( orderItemRequest.getSize() );
        orderItem.color( orderItemRequest.getColor() );
        orderItem.quantity( orderItemRequest.getQuantity() );

        return orderItem.build();
    }

    protected List<OrderItemResponse> orderItemListToOrderItemResponseList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemResponse> list1 = new ArrayList<OrderItemResponse>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toOrderItemResponse( orderItem ) );
        }

        return list1;
    }

    protected List<OrderItem> orderItemRequestListToOrderItemList(List<OrderItemRequest> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItem> list1 = new ArrayList<OrderItem>( list.size() );
        for ( OrderItemRequest orderItemRequest : list ) {
            list1.add( toOrderItem( orderItemRequest ) );
        }

        return list1;
    }
}
