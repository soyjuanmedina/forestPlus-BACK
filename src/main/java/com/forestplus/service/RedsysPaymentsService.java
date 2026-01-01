package com.forestplus.service;

import com.forestplus.dto.request.RedsysNotificationRequest;
import com.forestplus.dto.response.RedsysPaymentResponse;

public interface RedsysPaymentsService {

    /**
     * Genera los parámetros necesarios para redirigir al usuario a Redsys
     */
    RedsysPaymentResponse generateRedsysPayment(Long orderId);

    /**
     * Maneja la notificación enviada por Redsys tras el pago
     */
    void handleRedsysNotification(RedsysNotificationRequest notification);
}
