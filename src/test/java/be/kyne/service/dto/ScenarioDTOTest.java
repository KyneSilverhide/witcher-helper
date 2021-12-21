package be.kyne.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import be.kyne.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScenarioDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScenarioDTO.class);
        ScenarioDTO scenarioDTO1 = new ScenarioDTO();
        scenarioDTO1.setId(1L);
        ScenarioDTO scenarioDTO2 = new ScenarioDTO();
        assertThat(scenarioDTO1).isNotEqualTo(scenarioDTO2);
        scenarioDTO2.setId(scenarioDTO1.getId());
        assertThat(scenarioDTO1).isEqualTo(scenarioDTO2);
        scenarioDTO2.setId(2L);
        assertThat(scenarioDTO1).isNotEqualTo(scenarioDTO2);
        scenarioDTO1.setId(null);
        assertThat(scenarioDTO1).isNotEqualTo(scenarioDTO2);
    }
}
