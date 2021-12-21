package be.kyne.repository;

import be.kyne.domain.Scenario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Scenario entity.
 */
@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long>, JpaSpecificationExecutor<Scenario> {
    @Query(
        value = "select distinct scenario from Scenario scenario left join fetch scenario.players",
        countQuery = "select count(distinct scenario) from Scenario scenario"
    )
    Page<Scenario> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct scenario from Scenario scenario left join fetch scenario.players")
    List<Scenario> findAllWithEagerRelationships();

    @Query("select scenario from Scenario scenario left join fetch scenario.players where scenario.id =:id")
    Optional<Scenario> findOneWithEagerRelationships(@Param("id") Long id);
}
