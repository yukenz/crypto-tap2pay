package id.co.awan.tap2pay.repository;

import id.co.awan.tap2pay.model.entity.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, String> {

    Optional<Terminal> findByIdAndKey(String id, String key);

}
