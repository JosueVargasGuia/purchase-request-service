package com.nttdata.purchaserequest.model;

import java.util.List;

import com.nttdata.purchaserequest.entity.PurchaseRequest;
import com.nttdata.purchaserequest.entity.StatusValidate;
 

 
 
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
public class PurchaseRespose {
	List<String> error;
	StatusValidate status;
	PurchaseRequest purchaseRequest;
}
