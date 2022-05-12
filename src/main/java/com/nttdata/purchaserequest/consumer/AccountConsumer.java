package com.nttdata.purchaserequest.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.nttdata.purchaserequest.entity.PurchaseStatus;
import com.nttdata.purchaserequest.entity.TypeOfPayment;
import com.nttdata.purchaserequest.model.PurchaseRequestKafka;
import com.nttdata.purchaserequest.service.PurchaseRequestService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class AccountConsumer {
	@Autowired
	PurchaseRequestService requestService;

	@KafkaListener(topics = "${api.kafka-uri.account-topic-respose}", groupId = "group_id")
	public void accountConsumer(PurchaseRequestKafka purchaseRequestKafka) {
		log.info("Mensaje recivido account-topic-respose [AccountConsumer]:" + purchaseRequestKafka.toString());
		this.requestService.findById(purchaseRequestKafka.getIdPurchaseRequest()).flatMap(e -> {
			if (purchaseRequestKafka.getTypeOfPayment() == TypeOfPayment.yanki) {
				e.setIdWallet(purchaseRequestKafka.getIdWallet());
			}
			if (purchaseRequestKafka.getTypeOfPayment() == TypeOfPayment.transfer) {
				e.setIdBankAccount(purchaseRequestKafka.getIdBankAccount());
			}
			log.info("Actualizando account en PurchaseReques[customerConsumer] " + e.toString());
			return this.requestService.update(e);
		}).subscribe();
	}
}
