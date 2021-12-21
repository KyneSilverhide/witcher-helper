package be.kyne.service;

import be.kyne.domain.*; // for static metamodels
import be.kyne.domain.Scenario;
import be.kyne.repository.ScenarioRepository;
import be.kyne.service.criteria.ScenarioCriteria;
import be.kyne.service.dto.ScenarioDTO;
import be.kyne.service.mapper.ScenarioMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Scenario} entities in the database.
 * The main input is a {@link ScenarioCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScenarioDTO} or a {@link Page} of {@link ScenarioDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScenarioQueryService extends QueryService<Scenario> {

    private final Logger log = LoggerFactory.getLogger(ScenarioQueryService.class);

    private final ScenarioRepository scenarioRepository;

    private final ScenarioMapper scenarioMapper;

    public ScenarioQueryService(ScenarioRepository scenarioRepository, ScenarioMapper scenarioMapper) {
        this.scenarioRepository = scenarioRepository;
        this.scenarioMapper = scenarioMapper;
    }

    /**
     * Return a {@link List} of {@link ScenarioDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScenarioDTO> findByCriteria(ScenarioCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Scenario> specification = createSpecification(criteria);
        return scenarioMapper.toDto(scenarioRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ScenarioDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ScenarioDTO> findByCriteria(ScenarioCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Scenario> specification = createSpecification(criteria);
        return scenarioRepository.findAll(specification, page).map(scenarioMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScenarioCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Scenario> specification = createSpecification(criteria);
        return scenarioRepository.count(specification);
    }

    /**
     * Function to convert {@link ScenarioCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Scenario> createSpecification(ScenarioCriteria criteria) {
        Specification<Scenario> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Scenario_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Scenario_.title));
            }
            if (criteria.getMapCoords() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMapCoords(), Scenario_.mapCoords));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Scenario_.date));
            }
            if (criteria.getMapIcon() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMapIcon(), Scenario_.mapIcon));
            }
            if (criteria.getCampaignId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCampaignId(), root -> root.join(Scenario_.campaign, JoinType.LEFT).get(Campaign_.id))
                    );
            }
            if (criteria.getPlayersId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPlayersId(), root -> root.join(Scenario_.players, JoinType.LEFT).get(Player_.id))
                    );
            }
        }
        return specification;
    }
}
