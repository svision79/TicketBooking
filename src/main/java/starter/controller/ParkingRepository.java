package starter.controller;

import org.springframework.data.mongodb.repository.MongoRepository;
import Object.Car;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface ParkingRepository extends MongoRepository<Car, String> {
}
