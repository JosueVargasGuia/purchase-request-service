package com.nttdata.purchaserequest.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.nttdata.purchaserequest.entity.PurchaseStatus;
import com.nttdata.purchaserequest.entity.TypeOfPayment;
import com.nttdata.purchaserequest.model.PurchaseRequestKafka;
import com.nttdata.purchaserequest.service.PurchaseRequestService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CustomerConsumer {
	@Autowired
	PurchaseRequestService requestService;
	@Autowired
	KafkaTemplate<String, PurchaseRequestKafka> kafkaTemplate;
	@Value("${api.kafka-uri.account-wallet-topic}")
	String purchaseWalletTopic;

	@Value("${api.kafka-uri.account-bank-topic}")
	String purchaseAccountTopic;

	@KafkaListener(topics = "${api.kafka-uri.purchase-topic-respose}", groupId = "group_id")
	public void customerConsumer(PurchaseRequestKafka purchaseRequestKafka) {
		log.info("Mensaje recivido[customerConsumer]:" + purchaseRequestKafka.toString());
		this.requestService.findById(purchaseRequestKafka.getIdPurchaseRequest()).flatMap(e -> {
			e.setCustomerOrigin(purchaseRequestKafka.getCustomerOrigin());
			log.info("Actualizando custormer en PurchaseReques[customerConsumer] " + e.toString());
			return this.requestService.update(e).map(savecustomer -> {
				if (savecustomer.getTypeOfPayment() == TypeOfPayment.yanki) {
					log.info("Send Kafka[customerConsumer] "+purchaseWalletTopic+" -->"+  purchaseRequestKafka.toString());
					this.kafkaTemplate.send(purchaseWalletTopic, purchaseRequestKafka);
				}
				if (savecustomer.getTypeOfPayment() == TypeOfPayment.transfer) {
					log.info("Send Kafka[customerConsumer] "+purchaseAccountTopic+" -->"+  purchaseRequestKafka.toString());
					this.kafkaTemplate.send(purchaseAccountTopic, purchaseRequestKafka);
				}
				return savecustomer;
			});
		}).subscribe();
	}

	@KafkaListener(topics = "${api.kafka-uri.purchase-agree-topic-respose}", groupId = "group_id")
	public void customerAgreeConsumer(PurchaseRequestKafka purchaseRequestKafka) {
		log.info("Mensaje recivido[customerConsumer]:" + purchaseRequestKafka.toString());
		this.requestService.findById(purchaseRequestKafka.getIdPurchaseRequest()).flatMap(e -> {
			e.setCustomerDestiny(purchaseRequestKafka.getCustomerDestiny());
			e.setPurchaseStatus(PurchaseStatus.acceptance);
			log.info("Actualizando custormer en PurchaseReques[customerConsumer] " + e.toString());
			return this.requestService.update(e);
		}).subscribe();
	}
}
