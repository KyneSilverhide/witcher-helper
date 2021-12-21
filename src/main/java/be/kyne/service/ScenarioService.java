package be.kyne.service;

import be.kyne.domain.Scenario;
import be.kyne.repository.ScenarioRepository;
import be.kyne.service.dto.ScenarioDTO;
import be.kyne.service.mapper.ScenarioMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Scenario}.
 */
@Service
@Transactional
public class ScenarioService {

    private final Logger log = LoggerFactory.getLogger(ScenarioService.class);

    private final ScenarioRepository scenarioRepository;

    private final ScenarioMapper scenarioMapper;

    public ScenarioService(ScenarioRepository scenarioRepository, ScenarioMapper scenarioMapper) {
        this.scenarioRepository = scenarioRepository;
        this.scenarioMapper = scenarioMapper;
    }

    /**
     * Save a scenario.
     *
     * @param scenarioDTO the entity to save.
     * @return the persisted entity.
     */
    public ScenarioDTO save(ScenarioDTO scenarioDTO) {
        log.debug("Request to save Scenario : {}", scenarioDTO);
        Scenario scenario = scenarioMapper.toEntity(scenarioDTO);
        scenario = scenarioRepository.save(scenario);
        return scenarioMapper.toDto(scenario);
    }

    /**
     * Partially update a scenario.
     *
     * @param scenarioDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ScenarioDTO> partialUpdate(ScenarioDTO scenarioDTO) {
        log.debug("Request to partially update Scenario : {}", scenarioDTO);

        return scenarioRepository
            .findById(scenarioDTO.getId())
            .map(existingScenario -> {
                scenarioMapper.partialUpdate(existingScenario, scenarioDTO);

                return existingScenario;
            })
            .map(scenarioRepository::save)
            .map(scenarioMapper::toDto);
    }

    /**
     * Get all the scenarios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ScenarioDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Scenarios");
        return scenarioRepository.findAll(pageable).map(scenarioMapper::toDto);
    }

    /**
     * Get all the scenarios with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ScenarioDTO> findAllWithEagerRelationships(Pageable pageable) {
        return scenarioRepository.findAllWithEagerRelationships(pageable).map(scenarioMapper::toDto);
    }

    /**
     * Get one scenario by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ScenarioDTO> findOne(Long id) {
        log.debug("Request to get Scenario : {}", id);
        return scenarioRepository.findOneWithEagerRelationships(id).map(scenarioMapper::toDto);
    }

    /**
     * Delete the scenario by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Scenario : {}", id);
        scenarioRepository.deleteById(id);
    }
}
