package id.co.awan.tap2pay.repository;

import id.co.awan.tap2pay.model.entity.Hsm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HsmRepository extends JpaRepository<Hsm, String> {

    Boolean existsByOwnerAddress(String ownerAddress);

    List<Hsm> findAllByOwnerAddress(String ownerAddress);

    Optional<Hsm> findByIdAndPinAndOwnerAddress(String id, String pin, String ownerAddress);

    Optional<Hsm> findByIdAndPin(String id, String pin);

}
