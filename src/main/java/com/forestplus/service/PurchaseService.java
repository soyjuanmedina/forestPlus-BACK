package com.forestplus.service;

import com.forestplus.dto.request.PurchaseRequest;
import com.forestplus.dto.response.PurchaseResponse;

public interface PurchaseService {

	PurchaseResponse processPurchase(PurchaseRequest req);
}
