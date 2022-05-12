package com.nttdata.purchaserequest.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.nttdata.purchaserequest.entity.PurchaseRequest;
import com.nttdata.purchaserequest.entity.PurchaseStatus;
import com.nttdata.purchaserequest.entity.StatusValidate;
import com.nttdata.purchaserequest.entity.TypeOfPayment;
import com.nttdata.purchaserequest.model.AgreePurchase;
import com.nttdata.purchaserequest.model.PurchaseRequestKafka;
import com.nttdata.purchaserequest.model.PurchaseRespose;
import com.nttdata.purchaserequest.model.StartPurchase;
import com.nttdata.purchaserequest.respository.PurchaseRequestRepository;
import com.nttdata.purchaserequest.service.PurchaseRequestService;

import ch.qos.logback.core.status.Status;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class PurchaseRequestServiceImpl implements PurchaseRequestService {
	@Autowired
	PurchaseRequestRepository requestRepository;
	@Autowired
	KafkaTemplate<String, PurchaseRequestKafka> kafkaTemplate;

	@Override
	public Flux<PurchaseRequest> findAll() {
		return requestRepository.findAll()
				.sort((objA, objB) -> objA.getIdPurchaseRequest().compareTo(objB.getIdPurchaseRequest()));
	}

	@Value("${api.kafka-uri.purchase-topic}")
	String purchaseTopic;
	@Value("${api.kafka-uri.purchase-agree-topic}")
	String purchaseAgreeTopic;
	@Value("${api.kafka-uri.exchange-topic}")
	String exchangeTopic;

	@Override
	public Mono<PurchaseRequest> findById(Long idPurchaseRequest) {
		return requestRepository.findById(idPurchaseRequest);

	}

	@Override
	public Mono<PurchaseRequest> save(PurchaseRequest purchaseRequest) {
		Long count = this.findAll().collect(Collectors.counting()).blockOptional().get();
		Long idPurchaseRequest;
		if (count != null) {
			if (count <= 0) {
				idPurchaseRequest = Long.valueOf(0);
			} else {
				idPurchaseRequest = this.findAll()
						.collect(Collectors.maxBy(Comparator.comparing(PurchaseRequest::getIdPurchaseRequest)))
						.blockOptional().get().get().getIdPurchaseRequest();
			}
		} else {
			idPurchaseRequest = Long.valueOf(0);

		}
		purchaseRequest.setIdPurchaseRequest(idPurchaseRequest + 1);
		purchaseRequest.setCreationDate(Calendar.getInstance().getTime());
		return requestRepository.insert(purchaseRequest);
	}

	@Override
	public Mono<PurchaseRequest> update(PurchaseRequest purchaseRequest) {
		return requestRepository.save(purchaseRequest);
	}

	@Override
	public Mono<Void> delete(Long idPurchaseRequest) {

		return requestRepository.deleteById(idPurchaseRequest);
	}

	@Override
	public Mono<PurchaseRespose> requestThePurchase(StartPurchase startPurchase) {
		return Mono.just(startPurchase).map(e -> {
			log.info("Registrando el PurchaseRequest:" + e.toString());
			PurchaseRequest purchaseRequest = new PurchaseRequest();
			purchaseRequest.setAmountBitcoin(e.getAmountBitcoin());
			purchaseRequest.setCustomerOrigin(e.getCustomer());
			purchaseRequest.setTypeOfPayment(e.getTypeOfPayment());

			return purchaseRequest;
		}).flatMap(purchaseRequestSave -> {
			PurchaseRespose purchaseRespose = new PurchaseRespose();
			List<String> erros = new ArrayList<String>();
			if (purchaseRequestSave.getCustomerOrigin().getTypeDocument() != null
					|| purchaseRequestSave.getCustomerOrigin().getDocumentNumber() != null
					|| purchaseRequestSave.getCustomerOrigin().getPhoneNumber() != null
					|| purchaseRequestSave.getCustomerOrigin().getEmailAddress() != null) {
				purchaseRequestSave.setPurchaseStatus(PurchaseStatus.request);
				return this.save(purchaseRequestSave).map(saveObj -> {
					purchaseRespose.setStatus(StatusValidate.success);
					purchaseRespose.setPurchaseRequest(purchaseRequestSave);
					log.info("Registrando el PurchaseRequest:" + saveObj.toString());
					log.info("Send kafka:" + purchaseTopic + " -->" + purchaseRespose);
					PurchaseRequestKafka requestKafka = new PurchaseRequestKafka();
					requestKafka.setIdPurchaseRequest(saveObj.getIdPurchaseRequest());
					requestKafka.setCustomerOrigin(saveObj.getCustomerOrigin());
					requestKafka.setAmountBitcoin(saveObj.getAmountBitcoin());
					requestKafka.setTypeOfPayment(saveObj.getTypeOfPayment());					
					this.kafkaTemplate.send(purchaseTopic, requestKafka);
					
					log.info("Eviando mensaje para exchange-rate");
					this.kafkaTemplate.send(exchangeTopic, requestKafka);
					
					return purchaseRespose;
				});
			} else {
				erros.add("Debe ingresar el Nro de documento,Nro de celular o correo electronico.");
				purchaseRespose.setError(erros);
				purchaseRespose.setStatus(StatusValidate.error);
				return Mono.just(purchaseRespose);
			}

		});

	}

	@Override
	public Mono<PurchaseRespose> agreePurchase(AgreePurchase agreePurchase) {
		return Mono.just(agreePurchase).map(e -> {
			PurchaseRequest purchaseRequest = new PurchaseRequest();
			purchaseRequest.setIdPurchaseRequest(e.getIdPurchaseRequest());
			purchaseRequest.setCustomerDestiny(agreePurchase.getCustomer());
			return e;
		}).flatMap(obj -> {
			PurchaseRespose purchaseRespose = new PurchaseRespose();
			purchaseRespose.setStatus(StatusValidate.error);
			purchaseRespose.setError(new ArrayList<String>());
			purchaseRespose.getError().add("El codigo de compra no existe");
			return this.findById(obj.getIdPurchaseRequest()).map(purchase -> {
				purchaseRespose.setError(new ArrayList<String>());
				if (purchase.getPurchaseStatus() == PurchaseStatus.request) {
					purchaseRespose.setStatus(StatusValidate.success);
					PurchaseRequestKafka requestKafka = new PurchaseRequestKafka();
					requestKafka.setIdPurchaseRequest(purchase.getIdPurchaseRequest());
					requestKafka.setCustomerDestiny(agreePurchase.getCustomer());
					log.info("Asignando el PurchaseRequest:" + purchase.toString());
					log.info("Send kafka:" + purchaseAgreeTopic + " -->" + purchase);
					this.kafkaTemplate.send(purchaseAgreeTopic, requestKafka);
				} else {
					purchaseRespose.setStatus(StatusValidate.error);
					purchaseRespose.getError().add("El item de compra no se encuentra disponible para la transaccion.");
				}

				return purchaseRespose;
			}).switchIfEmpty(Mono.just(purchaseRespose));

		});

	}
}
