package com.nttdata.purchaserequest.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.nttdata.purchaserequest.entity.TypeOfPayment;
import com.nttdata.purchaserequest.model.PurchaseRequestKafka;
import com.nttdata.purchaserequest.service.PurchaseRequestService;

import lombok.extern.log4j.Log4j2;
@Log4j2
@Component
public class ExchangeConsumer {
	@Autowired
	PurchaseRequestService requestService;

	@KafkaListener(topics = "${api.kafka-uri.exchange-topic-respose}", groupId = "group_id")
	public void accountConsumer(PurchaseRequestKafka purchaseRequestKafka) {
		this.requestService.findById(purchaseRequestKafka.getIdPurchaseRequest()).flatMap(e -> {
			e.setAmountInCurrency(purchaseRequestKafka.getAmountInCurrency());
			log.info("Actualizando AmountInCurrency en PurchaseReques[customerConsumer] " + e.toString());
			return this.requestService.update(e);
		}).subscribe();
	}
}
