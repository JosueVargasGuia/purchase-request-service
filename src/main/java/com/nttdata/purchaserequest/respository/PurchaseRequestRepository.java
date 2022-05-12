package com.nttdata.purchaserequest.respository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.purchaserequest.entity.PurchaseRequest;
@Repository
public interface PurchaseRequestRepository extends ReactiveMongoRepository<PurchaseRequest,Long> {

}
