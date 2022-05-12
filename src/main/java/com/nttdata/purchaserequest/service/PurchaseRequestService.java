package com.nttdata.purchaserequest.service;

import com.nttdata.purchaserequest.entity.PurchaseRequest;
import com.nttdata.purchaserequest.model.AgreePurchase;
import com.nttdata.purchaserequest.model.PurchaseRespose;
import com.nttdata.purchaserequest.model.StartPurchase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PurchaseRequestService {
	Flux<PurchaseRequest> findAll();

	Mono<PurchaseRequest> findById(Long idPurchaseRequest);

	Mono<PurchaseRequest> save(PurchaseRequest purchaseRequest);

	Mono<PurchaseRequest> update(PurchaseRequest purchaseRequest);

	Mono<Void> delete(Long idPurchaseRequest);

	Mono<PurchaseRespose> requestThePurchase(StartPurchase startPurchase);

	Mono<PurchaseRespose> agreePurchase(AgreePurchase agreePurchase);
}
