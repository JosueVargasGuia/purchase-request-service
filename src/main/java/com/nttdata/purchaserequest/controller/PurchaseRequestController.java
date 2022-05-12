package com.nttdata.purchaserequest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.purchaserequest.entity.PurchaseRequest;
import com.nttdata.purchaserequest.model.AgreePurchase;
import com.nttdata.purchaserequest.model.PurchaseRespose;
import com.nttdata.purchaserequest.model.StartPurchase;
import com.nttdata.purchaserequest.service.PurchaseRequestService;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping("/api/v1/purchaserequest")
public class PurchaseRequestController {
	@Autowired
	PurchaseRequestService requestService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<PurchaseRequest> findAll() {
		return requestService.findAll();

	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ResponseEntity<PurchaseRequest>> save(@RequestBody PurchaseRequest purchaseRequest) {
		return requestService.save(purchaseRequest).map(_purchaseRequest -> ResponseEntity.ok().body(_purchaseRequest))
				.onErrorResume(e -> {
					log.info("Error:" + e.getMessage());
					return Mono.just(ResponseEntity.badRequest().build());
				});
	}

	@GetMapping("/{idPurchaseRequest}")
	public Mono<ResponseEntity<PurchaseRequest>> findById(
			@PathVariable(name = "idPurchaseRequest") Long idPurchaseRequest) {
		return requestService.findById(idPurchaseRequest)
				.map(purchaseRequest -> ResponseEntity.ok().body(purchaseRequest)).onErrorResume(e -> {
					log.info("Error:" + e.getMessage());
					return Mono.just(ResponseEntity.badRequest().build());
				}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@PutMapping
	public Mono<ResponseEntity<PurchaseRequest>> update(@RequestBody PurchaseRequest purchaseRequest) {
		Mono<PurchaseRequest> mono = requestService.findById(purchaseRequest.getIdPurchaseRequest())
				.flatMap(objPurchaseRequest -> {
					return requestService.update(purchaseRequest);
				});
		return mono.map(_purchaseRequest -> {
			return ResponseEntity.ok().body(_purchaseRequest);
		}).onErrorResume(e -> {
			log.info("Error:" + e.getMessage());
			return Mono.just(ResponseEntity.badRequest().build());
		}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@DeleteMapping("/{idPurchaseRequest}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable(name = "idPurchaseRequest") Long idPurchaseRequest) {
		Mono<PurchaseRequest> _purchaseRequest = requestService.findById(idPurchaseRequest);
		_purchaseRequest.subscribe();
		PurchaseRequest purchaseRequest = _purchaseRequest.toFuture().join();
		if (purchaseRequest != null) {
			return requestService.delete(idPurchaseRequest).map(r -> ResponseEntity.ok().<Void>build());
		} else {
			return Mono.just(ResponseEntity.noContent().build());
		}

	}

	@PostMapping("/requestThePurchase")
	public Mono<ResponseEntity<PurchaseRespose>> requestThePurchase(@RequestBody StartPurchase startPurchase) {
		return requestService.requestThePurchase(startPurchase)
				.map(_purchaseRequest -> ResponseEntity.ok().body(_purchaseRequest)).onErrorResume(e -> {
					log.info("Error:" + e.getMessage());
					return Mono.just(ResponseEntity.badRequest().build());
				});
	}

	@PostMapping("/agreePurchase")
	public Mono<ResponseEntity<PurchaseRespose>> requestThePurchase(@RequestBody AgreePurchase agreePurchase) {
		return requestService.agreePurchase(agreePurchase)
				.map(_agreePurchase -> ResponseEntity.ok().body(_agreePurchase)).onErrorResume(e -> {
					log.info("Error:" + e.getMessage());
					return Mono.just(ResponseEntity.badRequest().build());
				});
	}
}
