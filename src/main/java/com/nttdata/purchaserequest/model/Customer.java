package com.nttdata.purchaserequest.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
 
public class Customer {
	
 
	private Long idCustomer;
	private String firstname;
	private String lastname;
	private String emailAddress;
	private String phoneNumber;
	private String imeiPhone;
	private String homeAddress;
	private TypeDocument typeDocument;
	private String documentNumber;	
	private TypeCustomer typeCustomer;	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss") 
	private Date creationDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private Date dateModified;
	
}
