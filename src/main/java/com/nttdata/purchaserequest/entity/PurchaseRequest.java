package com.nttdata.purchaserequest.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nttdata.purchaserequest.model.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document(collection = "purchase-request")
public class PurchaseRequest {
	@Id
	private Long idPurchaseRequest;
	private Customer customerOrigin;
	private TypeOfPayment typeOfPayment;
	private Customer customerDestiny;
	private Double amountBitcoin;
	private Double conversionRateAmount;
	private Double amountInCurrency;
	private PurchaseStatus purchaseStatus;
	private Long idWallet;
	private Long idBankAccount;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private Date creationDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private Date dateModified;
}
