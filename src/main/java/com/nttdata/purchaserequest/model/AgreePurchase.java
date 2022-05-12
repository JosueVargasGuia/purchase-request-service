package com.nttdata.purchaserequest.model;

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
public class AgreePurchase {
	Customer customer;
	private Long idPurchaseRequest;
}
