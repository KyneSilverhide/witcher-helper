package be.kyne.web.rest;

import be.kyne.repository.ScenarioRepository;
import be.kyne.service.ScenarioQueryService;
import be.kyne.service.ScenarioService;
import be.kyne.service.criteria.ScenarioCriteria;
import be.kyne.service.dto.ScenarioDTO;
import be.kyne.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link be.kyne.domain.Scenario}.
 */
@RestController
@RequestMapping("/api")
public class ScenarioResource {

    private final Logger log = LoggerFactory.getLogger(ScenarioResource.class);

    private static final String ENTITY_NAME = "scenario";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScenarioService scenarioService;

    private final ScenarioRepository scenarioRepository;

    private final ScenarioQueryService scenarioQueryService;

    public ScenarioResource(
        ScenarioService scenarioService,
        ScenarioRepository scenarioRepository,
        ScenarioQueryService scenarioQueryService
    ) {
        this.scenarioService = scenarioService;
        this.scenarioRepository = scenarioRepository;
        this.scenarioQueryService = scenarioQueryService;
    }

    /**
     * {@code POST  /scenarios} : Create a new scenario.
     *
     * @param scenarioDTO the scenarioDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scenarioDTO, or with status {@code 400 (Bad Request)} if the scenario has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/scenarios")
    public ResponseEntity<ScenarioDTO> createScenario(@Valid @RequestBody ScenarioDTO scenarioDTO) throws URISyntaxException {
        log.debug("REST request to save Scenario : {}", scenarioDTO);
        if (scenarioDTO.getId() != null) {
            throw new BadRequestAlertException("A new scenario cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ScenarioDTO result = scenarioService.save(scenarioDTO);
        return ResponseEntity
            .created(new URI("/api/scenarios/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /scenarios/:id} : Updates an existing scenario.
     *
     * @param id the id of the scenarioDTO to save.
     * @param scenarioDTO the scenarioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scenarioDTO,
     * or with status {@code 400 (Bad Request)} if the scenarioDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scenarioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/scenarios/{id}")
    public ResponseEntity<ScenarioDTO> updateScenario(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScenarioDTO scenarioDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Scenario : {}, {}", id, scenarioDTO);
        if (scenarioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scenarioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scenarioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ScenarioDTO result = scenarioService.save(scenarioDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scenarioDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /scenarios/:id} : Partial updates given fields of an existing scenario, field will ignore if it is null
     *
     * @param id the id of the scenarioDTO to save.
     * @param scenarioDTO the scenarioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scenarioDTO,
     * or with status {@code 400 (Bad Request)} if the scenarioDTO is not valid,
     * or with status {@code 404 (Not Found)} if the scenarioDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the scenarioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/scenarios/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ScenarioDTO> partialUpdateScenario(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScenarioDTO scenarioDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Scenario partially : {}, {}", id, scenarioDTO);
        if (scenarioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scenarioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scenarioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ScenarioDTO> result = scenarioService.partialUpdate(scenarioDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scenarioDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /scenarios} : get all the scenarios.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scenarios in body.
     */
    @GetMapping("/scenarios")
    public ResponseEntity<List<ScenarioDTO>> getAllScenarios(ScenarioCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Scenarios by criteria: {}", criteria);
        Page<ScenarioDTO> page = scenarioQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /scenarios/count} : count all the scenarios.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/scenarios/count")
    public ResponseEntity<Long> countScenarios(ScenarioCriteria criteria) {
        log.debug("REST request to count Scenarios by criteria: {}", criteria);
        return ResponseEntity.ok().body(scenarioQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /scenarios/:id} : get the "id" scenario.
     *
     * @param id the id of the scenarioDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scenarioDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/scenarios/{id}")
    public ResponseEntity<ScenarioDTO> getScenario(@PathVariable Long id) {
        log.debug("REST request to get Scenario : {}", id);
        Optional<ScenarioDTO> scenarioDTO = scenarioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scenarioDTO);
    }

    /**
     * {@code DELETE  /scenarios/:id} : delete the "id" scenario.
     *
     * @param id the id of the scenarioDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/scenarios/{id}")
    public ResponseEntity<Void> deleteScenario(@PathVariable Long id) {
        log.debug("REST request to delete Scenario : {}", id);
        scenarioService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
