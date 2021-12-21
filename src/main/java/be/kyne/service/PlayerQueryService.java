package be.kyne.service;

import be.kyne.domain.*; // for static metamodels
import be.kyne.domain.Player;
import be.kyne.repository.PlayerRepository;
import be.kyne.service.criteria.PlayerCriteria;
import be.kyne.service.dto.PlayerDTO;
import be.kyne.service.mapper.PlayerMapper;
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
 * Service for executing complex queries for {@link Player} entities in the database.
 * The main input is a {@link PlayerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PlayerDTO} or a {@link Page} of {@link PlayerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PlayerQueryService extends QueryService<Player> {

    private final Logger log = LoggerFactory.getLogger(PlayerQueryService.class);

    private final PlayerRepository playerRepository;

    private final PlayerMapper playerMapper;

    public PlayerQueryService(PlayerRepository playerRepository, PlayerMapper playerMapper) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
    }

    /**
     * Return a {@link List} of {@link PlayerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> findByCriteria(PlayerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Player> specification = createSpecification(criteria);
        return playerMapper.toDto(playerRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PlayerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PlayerDTO> findByCriteria(PlayerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Player> specification = createSpecification(criteria);
        return playerRepository.findAll(specification, page).map(playerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PlayerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Player> specification = createSpecification(criteria);
        return playerRepository.count(specification);
    }

    /**
     * Function to convert {@link PlayerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Player> createSpecification(PlayerCriteria criteria) {
        Specification<Player> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Player_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Player_.name));
            }
            if (criteria.getRace() != null) {
                specification = specification.and(buildSpecification(criteria.getRace(), Player_.race));
            }
            if (criteria.getProfession() != null) {
                specification = specification.and(buildSpecification(criteria.getProfession(), Player_.profession));
            }
            if (criteria.getCampaignId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCampaignId(), root -> root.join(Player_.campaign, JoinType.LEFT).get(Campaign_.id))
                    );
            }
        }
        return specification;
    }
}
