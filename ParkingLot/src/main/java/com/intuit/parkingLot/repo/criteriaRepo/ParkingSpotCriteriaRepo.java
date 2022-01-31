package com.intuit.parkingLot.repo.criteriaRepo;

import com.intuit.parkingLot.dto.enums.SpotType;
import com.intuit.parkingLot.dto.enums.VehicleType;
import com.intuit.parkingLot.dto.response.ParkingTicket;
import com.intuit.parkingLot.entities.ParkingLotEntity;
import com.intuit.parkingLot.entities.ParkingSpotEntity;
import com.intuit.parkingLot.service.TicketGenerationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class ParkingSpotCriteriaRepo {

    private final Logger logger = LogManager.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TicketGenerationService ticketGenerationService;

    @Transactional
    public List<ParkingSpotEntity> getEmptySpotsForSpotTypeAndLevel(SpotType spotType, Integer level) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ParkingSpotEntity> query = builder.createQuery(ParkingSpotEntity.class);
        Root<ParkingSpotEntity> parkingSpotRoot = query.from(ParkingSpotEntity.class);

        Predicate p1 = builder.equal(parkingSpotRoot.get("operational"), Boolean.TRUE);
        Predicate p2 = builder.equal(parkingSpotRoot.get("empty"), Boolean.TRUE);
        Predicate p3 = builder.equal(parkingSpotRoot.get("spotType"), spotType.toString());
        Predicate p4 = builder.equal(parkingSpotRoot.get("level"), level);
        query.where(p1, p2, p3, p4);

        return entityManager.createQuery(query).getResultList();
    }

    @Transactional
    public ParkingSpotEntity getParkingSpotForSpotTypeOrderByLevel(SpotType spotType) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ParkingSpotEntity> query = builder.createQuery(ParkingSpotEntity.class);
        Root<ParkingSpotEntity> parkingSpotRoot = query.from(ParkingSpotEntity.class);

        Predicate p1 = builder.equal(parkingSpotRoot.get("operational"), Boolean.TRUE);
        Predicate p2 = builder.equal(parkingSpotRoot.get("empty"), Boolean.TRUE);
        Predicate p3 = builder.equal(parkingSpotRoot.get("spotType"), spotType.toString());
        query.where(p1, p2, p3);
        query.orderBy(builder.asc(parkingSpotRoot.get("level")));

        List<ParkingSpotEntity> parkingSpotEntityList = entityManager.createQuery(query).getResultList();
        if (CollectionUtils.isEmpty(parkingSpotEntityList)) {
            return null;
        }

        return parkingSpotEntityList.get(0);
    }

    @Transactional
    public ParkingTicket updateSpotAndGetTicket(List<Long> spotIds, VehicleType vehicleType, String vehicleRegNo, ParkingLotEntity parkingLotEntity) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<ParkingSpotEntity> update = builder.createCriteriaUpdate(ParkingSpotEntity.class);
        Root<ParkingSpotEntity> parkingSpotRoot = update.from(ParkingSpotEntity.class);

        update.set("empty", Boolean.FALSE);

        for (Long spotId : spotIds) {
            Predicate freePredicate = builder.equal(parkingSpotRoot.get("empty"), Boolean.TRUE);
            Predicate operationalPredicate = builder.equal(parkingSpotRoot.get("operational"), Boolean.TRUE);
            Predicate spotIdPredicate = builder.equal(parkingSpotRoot.get("id"), spotId);
            update.where(freePredicate, operationalPredicate, spotIdPredicate);
            int updateResult = entityManager.createQuery(update).executeUpdate();
            if (updateResult == 0) {
                throw new RuntimeException();
            }
            logger.info("Result of updateSpotIfOperationalAndFree {}, spotId {}", updateResult, spotId);
        }

        return ticketGenerationService.generateParkingTicket(spotIds, vehicleType, vehicleRegNo, parkingLotEntity);
    }

    @Transactional
    public void markSpotsFree(List<Long> spotIds) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<ParkingSpotEntity> update = builder.createCriteriaUpdate(ParkingSpotEntity.class);
        Root<ParkingSpotEntity> parkingSpotRoot = update.from(ParkingSpotEntity.class);

        for (Long spotId : spotIds) {
            update.set("empty", Boolean.TRUE);
            Predicate p1 = builder.equal(parkingSpotRoot.get("id"), spotId);
            Predicate p2 = builder.equal(parkingSpotRoot.get("empty"), Boolean.FALSE);
            update.where(p1, p2);
            int updateResult = entityManager.createQuery(update).executeUpdate();
            logger.info("Result of markSpotsFree {{}} for spotId {{}}", updateResult, spotId);

            if (updateResult == 0) {
                throw new RuntimeException();
            }
        }
    }

    @Transactional
    public int changeSpotAvailability(Integer level, Integer row, Integer col, Boolean operational) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<ParkingSpotEntity> update = builder.createCriteriaUpdate(ParkingSpotEntity.class);
        Root<ParkingSpotEntity> from = update.from(ParkingSpotEntity.class);

        update.set("operational", operational);
        Predicate p1 = builder.equal(from.get("empty"), Boolean.TRUE);
        Predicate p2 = builder.equal(from.get("level"), level);
        Predicate p3 = builder.equal(from.get("lineNumber"), row);
        Predicate p4 = builder.equal(from.get("position"), col);
        update.where(p1, p2, p3, p4);
        int result = entityManager.createQuery(update).executeUpdate();
        logger.info("Result of changeSpotAvailability is : {}", result);

        return result;
    }

    /**================================================== Private Methods =======================================================*/
}
