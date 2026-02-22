package com.forestplus.service;

import com.forestplus.dto.request.RedsysNotificationRequest;
import com.forestplus.dto.response.RedsysPaymentResponse;
import com.forestplus.entity.OrderEntity;
import com.forestplus.repository.OrdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedsysPaymentsServiceImplTest {

    @InjectMocks
    private RedsysPaymentsServiceImpl service;

    @Mock
    private OrdersRepository ordersRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // inyectar propiedades
        service.merchantCode = "123456789";
        service.terminal = "1";
        service.secretKey = "secret";
        service.redsysUrl = "https://redsys.test";
        service.urlOk = "https://ok.test";
        service.urlKo = "https://ko.test";
    }

    @Test
    void testGenerateRedsysPayment() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setTotalAmount(new BigDecimal("10.50"));

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        RedsysPaymentResponse response = service.generateRedsysPayment(1L);

        assertNotNull(response);
        assertEquals("https://redsys.test", response.getRedsysUrl());

        Map<String, String> params = response.getParameters();
        assertEquals("1050", params.get("Ds_Merchant_Amount")); // 10.50 * 100
        assertEquals("000000000001", params.get("Ds_Merchant_Order"));
        assertEquals("123456789", params.get("Ds_Merchant_MerchantCode"));
        assertEquals("1", params.get("Ds_Merchant_Terminal"));
        assertNotNull(params.get("Ds_Merchant_MerchantSignature"));
        assertEquals("https://ok.test", params.get("Ds_Merchant_UrlOK"));
        assertEquals("https://ko.test", params.get("Ds_Merchant_UrlKO"));
    }

    @Test
    void testGenerateRedsysPayment_OrderNotFound() {
        when(ordersRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.generateRedsysPayment(1L));
        assertEquals("Pedido no encontrado", ex.getMessage());
    }

    @Test
    void testHandleRedsysNotification_Paid() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setTotalAmount(new BigDecimal("10"));

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        RedsysNotificationRequest notification = new RedsysNotificationRequest();
        notification.setDs_Order("1");
        notification.setDs_Response("0000");
        // firma correcta según la lógica simplificada del servicio
        long amount = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue();
        notification.setDs_Signature(java.util.Base64.getEncoder()
                .encodeToString((service.merchantCode + "1" + amount).getBytes()));

        service.handleRedsysNotification(notification);

        assertEquals("paid", order.getStatus());
        verify(ordersRepository).save(order);
    }

    @Test
    void testHandleRedsysNotification_Failed() {
        OrderEntity order = new OrderEntity();
        order.setId(2L);
        order.setTotalAmount(new BigDecimal("5"));

        when(ordersRepository.findById(2L)).thenReturn(Optional.of(order));

        RedsysNotificationRequest notification = new RedsysNotificationRequest();
        notification.setDs_Order("2");
        notification.setDs_Response("9999"); // no 0000
        long amount = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue();
        notification.setDs_Signature(java.util.Base64.getEncoder()
                .encodeToString((service.merchantCode + "2" + amount).getBytes()));

        service.handleRedsysNotification(notification);

        assertEquals("failed", order.getStatus());
        verify(ordersRepository).save(order);
    }

    @Test
    void testHandleRedsysNotification_InvalidSignature() {
        OrderEntity order = new OrderEntity();
        order.setId(3L);
        order.setTotalAmount(new BigDecimal("5"));

        when(ordersRepository.findById(3L)).thenReturn(Optional.of(order));

        RedsysNotificationRequest notification = new RedsysNotificationRequest();
        notification.setDs_Order("3");
        notification.setDs_Response("0000");
        notification.setDs_Signature("invalidsignature");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.handleRedsysNotification(notification));
        assertEquals("Firma inválida", ex.getMessage());
    }

    @Test
    void testHandleRedsysNotification_OrderNotFound() {
        when(ordersRepository.findById(4L)).thenReturn(Optional.empty());

        RedsysNotificationRequest notification = new RedsysNotificationRequest();
        notification.setDs_Order("4");
        notification.setDs_Response("0000");
        notification.setDs_Signature("any");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.handleRedsysNotification(notification));
        assertEquals("Pedido no encontrado", ex.getMessage());
    }
}