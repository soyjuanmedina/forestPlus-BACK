package com.forestplus.dto.response;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedsysPaymentResponse {

    @Schema(description = "URL de Redsys donde redirigir al usuario")
    private String redsysUrl;

    @Schema(description = "Parámetros firmados necesarios para el pago")
    private Map<String, String> parameters; // Ds_Merchant_Amount, Ds_Merchant_Order, Ds_Merchant_MerchantSignature...
}
