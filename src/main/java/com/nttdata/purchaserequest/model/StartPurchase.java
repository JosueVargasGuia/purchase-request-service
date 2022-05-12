package com.nttdata.purchaserequest.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nttdata.purchaserequest.entity.PurchaseRequest;
import com.nttdata.purchaserequest.entity.PurchaseStatus;
import com.nttdata.purchaserequest.entity.TypeOfPayment;

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
public class  StartPurchase {
	private Customer customer;
	private Double amountBitcoin;
	private TypeOfPayment typeOfPayment;
	
}
