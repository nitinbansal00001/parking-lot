package com.intuit.parkingLot.repo.criteriaRepo;

import com.intuit.parkingLot.dto.enums.TicketStatus;
import com.intuit.parkingLot.entities.ParkingTicketEntity;
import com.intuit.parkingLot.exceptions.InvalidParkingTicketException;
import com.intuit.parkingLot.repo.nativeRepo.PricingEntityRepo;
import com.intuit.parkingLot.utils.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ParkingTicketCriteriaRepo {

    private final Logger logger = LogManager.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    protected ParkingSpotCriteriaRepo parkingSpotCriteriaRepo;

    @Autowired
    protected PricingEntityRepo pricingEntityRepo;

    @Transactional
    public ParkingTicketEntity getByTicketId(Long ticketId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ParkingTicketEntity> query = builder.createQuery(ParkingTicketEntity.class);
        Root<ParkingTicketEntity> from = query.from(ParkingTicketEntity.class);

        Predicate p1 = builder.equal(from.get("ticketId"), ticketId);
        query.where(p1);

        return entityManager.createQuery(query).getResultList().get(0);
    }

    @Transactional
    public List<ParkingTicketEntity> getActiveTicketForRegistrationNumber(String vehicleRegNo) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ParkingTicketEntity> query = builder.createQuery(ParkingTicketEntity.class);
        Root<ParkingTicketEntity> from = query.from(ParkingTicketEntity.class);

        Predicate p1 = builder.equal(from.get("vehicleRegNo"), vehicleRegNo);
        Predicate p2 = builder.equal(from.get("ticketStatus"), TicketStatus.ACTIVE.toString());
        query.where(p1, p2);

        return entityManager.createQuery(query).getResultList();
    }

    @Transactional
    public void markTicketPaidAndMakeSpotsFree(ParkingTicketEntity parkingTicketEntity) throws InvalidParkingTicketException {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<ParkingTicketEntity> update = builder.createCriteriaUpdate(ParkingTicketEntity.class);
        Root<ParkingTicketEntity> root = update.from(ParkingTicketEntity.class);

        Date exitTime = new Date();
        Double pricePerHour = pricingEntityRepo.findPerHourPriceForVehicleType(parkingTicketEntity.getVehicleType());
        Double amount = CommonUtils.calculateAmount(pricePerHour, parkingTicketEntity.getEntryTime(), exitTime);
        update.set("ticketStatus", TicketStatus.PAID.toString())
                .set("amount", amount)
                .set("exitTime", exitTime);

        Predicate idPredicate = builder.equal(root.get("ticketId"), parkingTicketEntity.getTicketId());
        update.where(idPredicate);

        int updateResult = entityManager.createQuery(update).executeUpdate();
        logger.info("Result of markTicketPaid is : {}", updateResult);

        if (updateResult == 0) {
            logger.info("No such ticket found");
            throw new InvalidParkingTicketException("Parking ticket does not exist");
        }

        List<Long> spotIdList = Arrays.stream(parkingTicketEntity.getSpotIds().split(",")).map(s -> Long.parseLong(s)).collect(Collectors.toList());
        parkingSpotCriteriaRepo.markSpotsFree(spotIdList);
    }
}
