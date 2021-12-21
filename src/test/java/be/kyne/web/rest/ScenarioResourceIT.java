package be.kyne.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.kyne.IntegrationTest;
import be.kyne.domain.Campaign;
import be.kyne.domain.Player;
import be.kyne.domain.Scenario;
import be.kyne.repository.ScenarioRepository;
import be.kyne.service.ScenarioService;
import be.kyne.service.criteria.ScenarioCriteria;
import be.kyne.service.dto.ScenarioDTO;
import be.kyne.service.mapper.ScenarioMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link ScenarioResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ScenarioResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_MAP_COORDS = "AAAAAAAAAA";
    private static final String UPDATED_MAP_COORDS = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_MAP_ICON = "AAAAAAAAAA";
    private static final String UPDATED_MAP_ICON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/scenarios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Mock
    private ScenarioRepository scenarioRepositoryMock;

    @Autowired
    private ScenarioMapper scenarioMapper;

    @Mock
    private ScenarioService scenarioServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScenarioMockMvc;

    private Scenario scenario;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scenario createEntity(EntityManager em) {
        Scenario scenario = new Scenario()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .mapCoords(DEFAULT_MAP_COORDS)
            .date(DEFAULT_DATE)
            .mapIcon(DEFAULT_MAP_ICON);
        return scenario;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scenario createUpdatedEntity(EntityManager em) {
        Scenario scenario = new Scenario()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .mapCoords(UPDATED_MAP_COORDS)
            .date(UPDATED_DATE)
            .mapIcon(UPDATED_MAP_ICON);
        return scenario;
    }

    @BeforeEach
    public void initTest() {
        scenario = createEntity(em);
    }

    @Test
    @Transactional
    void createScenario() throws Exception {
        int databaseSizeBeforeCreate = scenarioRepository.findAll().size();
        // Create the Scenario
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);
        restScenarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isCreated());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeCreate + 1);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testScenario.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testScenario.getMapCoords()).isEqualTo(DEFAULT_MAP_COORDS);
        assertThat(testScenario.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testScenario.getMapIcon()).isEqualTo(DEFAULT_MAP_ICON);
    }

    @Test
    @Transactional
    void createScenarioWithExistingId() throws Exception {
        // Create the Scenario with an existing ID
        scenario.setId(1L);
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        int databaseSizeBeforeCreate = scenarioRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScenarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setTitle(null);

        // Create the Scenario, which fails.
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        restScenarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isBadRequest());

        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMapCoordsIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setMapCoords(null);

        // Create the Scenario, which fails.
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        restScenarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isBadRequest());

        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMapIconIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setMapIcon(null);

        // Create the Scenario, which fails.
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        restScenarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isBadRequest());

        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllScenarios() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList
        restScenarioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scenario.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].mapCoords").value(hasItem(DEFAULT_MAP_COORDS)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].mapIcon").value(hasItem(DEFAULT_MAP_ICON)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllScenariosWithEagerRelationshipsIsEnabled() throws Exception {
        when(scenarioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restScenarioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(scenarioServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllScenariosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(scenarioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restScenarioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(scenarioServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getScenario() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get the scenario
        restScenarioMockMvc
            .perform(get(ENTITY_API_URL_ID, scenario.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(scenario.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.mapCoords").value(DEFAULT_MAP_COORDS))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.mapIcon").value(DEFAULT_MAP_ICON));
    }

    @Test
    @Transactional
    void getScenariosByIdFiltering() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        Long id = scenario.getId();

        defaultScenarioShouldBeFound("id.equals=" + id);
        defaultScenarioShouldNotBeFound("id.notEquals=" + id);

        defaultScenarioShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultScenarioShouldNotBeFound("id.greaterThan=" + id);

        defaultScenarioShouldBeFound("id.lessThanOrEqual=" + id);
        defaultScenarioShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllScenariosByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where title equals to DEFAULT_TITLE
        defaultScenarioShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the scenarioList where title equals to UPDATED_TITLE
        defaultScenarioShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllScenariosByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where title not equals to DEFAULT_TITLE
        defaultScenarioShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the scenarioList where title not equals to UPDATED_TITLE
        defaultScenarioShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllScenariosByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultScenarioShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the scenarioList where title equals to UPDATED_TITLE
        defaultScenarioShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllScenariosByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where title is not null
        defaultScenarioShouldBeFound("title.specified=true");

        // Get all the scenarioList where title is null
        defaultScenarioShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllScenariosByTitleContainsSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where title contains DEFAULT_TITLE
        defaultScenarioShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the scenarioList where title contains UPDATED_TITLE
        defaultScenarioShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllScenariosByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where title does not contain DEFAULT_TITLE
        defaultScenarioShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the scenarioList where title does not contain UPDATED_TITLE
        defaultScenarioShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllScenariosByMapCoordsIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where mapCoords equals to DEFAULT_MAP_COORDS
        defaultScenarioShouldBeFound("mapCoords.equals=" + DEFAULT_MAP_COORDS);

        // Get all the scenarioList where mapCoords equals to UPDATED_MAP_COORDS
        defaultScenarioShouldNotBeFound("mapCoords.equals=" + UPDATED_MAP_COORDS);
    }

    @Test
    @Transactional
    void getAllScenariosByMapCoordsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where mapCoords not equals to DEFAULT_MAP_COORDS
        defaultScenarioShouldNotBeFound("mapCoords.notEquals=" + DEFAULT_MAP_COORDS);

        // Get all the scenarioList where mapCoords not equals to UPDATED_MAP_COORDS
        defaultScenarioShouldBeFound("mapCoords.notEquals=" + UPDATED_MAP_COORDS);
    }

    @Test
    @Transactional
    void getAllScenariosByMapCoordsIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where mapCoords in DEFAULT_MAP_COORDS or UPDATED_MAP_COORDS
        defaultScenarioShouldBeFound("mapCoords.in=" + DEFAULT_MAP_COORDS + "," + UPDATED_MAP_COORDS);

        // Get all the scenarioList where mapCoords equals to UPDATED_MAP_COORDS
        defaultScenarioShouldNotBeFound("mapCoords.in=" + UPDATED_MAP_COORDS);
    }

    @Test
    @Transactional
    void getAllScenariosByMapCoordsIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where mapCoords is not null
        defaultScenarioShouldBeFound("mapCoords.specified=true");

        // Get all the scenarioList where mapCoords is null
        defaultScenarioShouldNotBeFound("mapCoords.specified=false");
    }

    @Test
    @Transactional
    void getAllScenariosByMapCoordsContainsSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where mapCoords contains DEFAULT_MAP_COORDS
        defaultScenarioShouldBeFound("mapCoords.contains=" + DEFAULT_MAP_COORDS);

        // Get all the scenarioList where mapCoords contains UPDATED_MAP_COORDS
        defaultScenarioShouldNotBeFound("mapCoords.contains=" + UPDATED_MAP_COORDS);
    }

    @Test
    @Transactional
    void getAllScenariosByMapCoordsNotContainsSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where mapCoords does not contain DEFAULT_MAP_COORDS
        defaultScenarioShouldNotBeFound("mapCoords.doesNotContain=" + DEFAULT_MAP_COORDS);

        // Get all the scenarioList where mapCoords does not contain UPDATED_MAP_COORDS
        defaultScenarioShouldBeFound("mapCoords.doesNotContain=" + UPDATED_MAP_COORDS);
    }

    @Test
    @Transactional
    void getAllScenariosByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where date equals to DEFAULT_DATE
        defaultScenarioShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the scenarioList where date equals to UPDATED_DATE
        defaultScenarioShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllScenariosByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where date not equals to DEFAULT_DATE
        defaultScenarioShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the scenarioList where date not equals to UPDATED_DATE
        defaultScenarioShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllScenariosByDateIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where date in DEFAULT_DATE or UPDATED_DATE
        defaultScenarioShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the scenarioList where date equals to UPDATED_DATE
        defaultScenarioShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllScenariosByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where date is not null
        defaultScenarioShouldBeFound("date.specified=true");

        // Get all the scenarioList where date is null
        defaultScenarioShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllScenariosByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where date is greater than or equal to DEFAULT_DATE
        defaultScenarioShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the scenarioList where date is greater than or equal to UPDATED_DATE
        defaultScenarioShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllScenariosByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where date is less than or equal to DEFAULT_DATE
        defaultScenarioShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the scenarioList where date is less than or equal to SMALLER_DATE
        defaultScenarioShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllScenariosByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where date is less than DEFAULT_DATE
        defaultScenarioShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the scenarioList where date is less than UPDATED_DATE
        defaultScenarioShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllScenariosByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where date is greater than DEFAULT_DATE
        defaultScenarioShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the scenarioList where date is greater than SMALLER_DATE
        defaultScenarioShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllScenariosByMapIconIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where mapIcon equals to DEFAULT_MAP_ICON
        defaultScenarioShouldBeFound("mapIcon.equals=" + DEFAULT_MAP_ICON);

        // Get all the scenarioList where mapIcon equals to UPDATED_MAP_ICON
        defaultScenarioShouldNotBeFound("mapIcon.equals=" + UPDATED_MAP_ICON);
    }

    @Test
    @Transactional
    void getAllScenariosByMapIconIsNotEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where mapIcon not equals to DEFAULT_MAP_ICON
        defaultScenarioShouldNotBeFound("mapIcon.notEquals=" + DEFAULT_MAP_ICON);

        // Get all the scenarioList where mapIcon not equals to UPDATED_MAP_ICON
        defaultScenarioShouldBeFound("mapIcon.notEquals=" + UPDATED_MAP_ICON);
    }

    @Test
    @Transactional
    void getAllScenariosByMapIconIsInShouldWork() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where mapIcon in DEFAULT_MAP_ICON or UPDATED_MAP_ICON
        defaultScenarioShouldBeFound("mapIcon.in=" + DEFAULT_MAP_ICON + "," + UPDATED_MAP_ICON);

        // Get all the scenarioList where mapIcon equals to UPDATED_MAP_ICON
        defaultScenarioShouldNotBeFound("mapIcon.in=" + UPDATED_MAP_ICON);
    }

    @Test
    @Transactional
    void getAllScenariosByMapIconIsNullOrNotNull() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where mapIcon is not null
        defaultScenarioShouldBeFound("mapIcon.specified=true");

        // Get all the scenarioList where mapIcon is null
        defaultScenarioShouldNotBeFound("mapIcon.specified=false");
    }

    @Test
    @Transactional
    void getAllScenariosByMapIconContainsSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where mapIcon contains DEFAULT_MAP_ICON
        defaultScenarioShouldBeFound("mapIcon.contains=" + DEFAULT_MAP_ICON);

        // Get all the scenarioList where mapIcon contains UPDATED_MAP_ICON
        defaultScenarioShouldNotBeFound("mapIcon.contains=" + UPDATED_MAP_ICON);
    }

    @Test
    @Transactional
    void getAllScenariosByMapIconNotContainsSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList where mapIcon does not contain DEFAULT_MAP_ICON
        defaultScenarioShouldNotBeFound("mapIcon.doesNotContain=" + DEFAULT_MAP_ICON);

        // Get all the scenarioList where mapIcon does not contain UPDATED_MAP_ICON
        defaultScenarioShouldBeFound("mapIcon.doesNotContain=" + UPDATED_MAP_ICON);
    }

    @Test
    @Transactional
    void getAllScenariosByCampaignIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);
        Campaign campaign;
        if (TestUtil.findAll(em, Campaign.class).isEmpty()) {
            campaign = CampaignResourceIT.createEntity(em);
            em.persist(campaign);
            em.flush();
        } else {
            campaign = TestUtil.findAll(em, Campaign.class).get(0);
        }
        em.persist(campaign);
        em.flush();
        scenario.setCampaign(campaign);
        scenarioRepository.saveAndFlush(scenario);
        Long campaignId = campaign.getId();

        // Get all the scenarioList where campaign equals to campaignId
        defaultScenarioShouldBeFound("campaignId.equals=" + campaignId);

        // Get all the scenarioList where campaign equals to (campaignId + 1)
        defaultScenarioShouldNotBeFound("campaignId.equals=" + (campaignId + 1));
    }

    @Test
    @Transactional
    void getAllScenariosByPlayersIsEqualToSomething() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);
        Player players;
        if (TestUtil.findAll(em, Player.class).isEmpty()) {
            players = PlayerResourceIT.createEntity(em);
            em.persist(players);
            em.flush();
        } else {
            players = TestUtil.findAll(em, Player.class).get(0);
        }
        em.persist(players);
        em.flush();
        scenario.addPlayers(players);
        scenarioRepository.saveAndFlush(scenario);
        Long playersId = players.getId();

        // Get all the scenarioList where players equals to playersId
        defaultScenarioShouldBeFound("playersId.equals=" + playersId);

        // Get all the scenarioList where players equals to (playersId + 1)
        defaultScenarioShouldNotBeFound("playersId.equals=" + (playersId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScenarioShouldBeFound(String filter) throws Exception {
        restScenarioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scenario.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].mapCoords").value(hasItem(DEFAULT_MAP_COORDS)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].mapIcon").value(hasItem(DEFAULT_MAP_ICON)));

        // Check, that the count call also returns 1
        restScenarioMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScenarioShouldNotBeFound(String filter) throws Exception {
        restScenarioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScenarioMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingScenario() throws Exception {
        // Get the scenario
        restScenarioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewScenario() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();

        // Update the scenario
        Scenario updatedScenario = scenarioRepository.findById(scenario.getId()).get();
        // Disconnect from session so that the updates on updatedScenario are not directly saved in db
        em.detach(updatedScenario);
        updatedScenario
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .mapCoords(UPDATED_MAP_COORDS)
            .date(UPDATED_DATE)
            .mapIcon(UPDATED_MAP_ICON);
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(updatedScenario);

        restScenarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scenarioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scenarioDTO))
            )
            .andExpect(status().isOk());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testScenario.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScenario.getMapCoords()).isEqualTo(UPDATED_MAP_COORDS);
        assertThat(testScenario.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testScenario.getMapIcon()).isEqualTo(UPDATED_MAP_ICON);
    }

    @Test
    @Transactional
    void putNonExistingScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        scenario.setId(count.incrementAndGet());

        // Create the Scenario
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scenarioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scenarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        scenario.setId(count.incrementAndGet());

        // Create the Scenario
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scenarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        scenario.setId(count.incrementAndGet());

        // Create the Scenario
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scenarioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScenarioWithPatch() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();

        // Update the scenario using partial update
        Scenario partialUpdatedScenario = new Scenario();
        partialUpdatedScenario.setId(scenario.getId());

        partialUpdatedScenario.mapCoords(UPDATED_MAP_COORDS).date(UPDATED_DATE).mapIcon(UPDATED_MAP_ICON);

        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScenario.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedScenario))
            )
            .andExpect(status().isOk());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testScenario.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testScenario.getMapCoords()).isEqualTo(UPDATED_MAP_COORDS);
        assertThat(testScenario.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testScenario.getMapIcon()).isEqualTo(UPDATED_MAP_ICON);
    }

    @Test
    @Transactional
    void fullUpdateScenarioWithPatch() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();

        // Update the scenario using partial update
        Scenario partialUpdatedScenario = new Scenario();
        partialUpdatedScenario.setId(scenario.getId());

        partialUpdatedScenario
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .mapCoords(UPDATED_MAP_COORDS)
            .date(UPDATED_DATE)
            .mapIcon(UPDATED_MAP_ICON);

        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScenario.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedScenario))
            )
            .andExpect(status().isOk());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testScenario.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScenario.getMapCoords()).isEqualTo(UPDATED_MAP_COORDS);
        assertThat(testScenario.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testScenario.getMapIcon()).isEqualTo(UPDATED_MAP_ICON);
    }

    @Test
    @Transactional
    void patchNonExistingScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        scenario.setId(count.incrementAndGet());

        // Create the Scenario
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scenarioDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(scenarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        scenario.setId(count.incrementAndGet());

        // Create the Scenario
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(scenarioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        scenario.setId(count.incrementAndGet());

        // Create the Scenario
        ScenarioDTO scenarioDTO = scenarioMapper.toDto(scenario);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(scenarioDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteScenario() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        int databaseSizeBeforeDelete = scenarioRepository.findAll().size();

        // Delete the scenario
        restScenarioMockMvc
            .perform(delete(ENTITY_API_URL_ID, scenario.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
