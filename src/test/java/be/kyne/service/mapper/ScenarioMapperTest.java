package be.kyne.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScenarioMapperTest {

    private ScenarioMapper scenarioMapper;

    @BeforeEach
    public void setUp() {
        scenarioMapper = new ScenarioMapperImpl();
    }
}
