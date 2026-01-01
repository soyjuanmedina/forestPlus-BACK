package com.forestplus.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedsysNotificationRequest {

    @Schema(description = "Número de pedido enviado a Redsys")
    private String Ds_Order;

    @Schema(description = "Código de respuesta de Redsys (0000 = aprobado)")
    private String Ds_Response;

    @Schema(description = "Firma enviada por Redsys para validar la notificación")
    private String Ds_Signature;

    // Puedes añadir otros campos que Redsys envíe si los necesitas
    // private String Ds_MerchantCode;
    // private String Ds_AuthorisationCode;
}
