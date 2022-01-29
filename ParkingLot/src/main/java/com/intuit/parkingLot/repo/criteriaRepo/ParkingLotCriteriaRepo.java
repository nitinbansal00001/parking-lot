package com.intuit.parkingLot.repo.criteriaRepo;

import com.intuit.parkingLot.entities.ParkingLotEntity;
import com.intuit.parkingLot.entities.ParkingSpotEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class ParkingLotCriteriaRepo {
    private final Logger logger = LogManager.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ParkingLotEntity getByParkingLotId(Long parkingLotId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ParkingLotEntity> query = builder.createQuery(ParkingLotEntity.class);
        Root<ParkingLotEntity> from = query.from(ParkingLotEntity.class);

        Predicate p1 = builder.equal(from.get("parkingLotId"), parkingLotId);
        query.where(p1);
        return entityManager.createQuery(query).getSingleResult();
    }
}
