package rostyk.stupnytskiy.andromeda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rostyk.stupnytskiy.andromeda.entity.Property;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
//    List<Property>
}
