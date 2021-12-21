package be.kyne.service.mapper;

import be.kyne.domain.Scenario;
import be.kyne.service.dto.ScenarioDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Scenario} and its DTO {@link ScenarioDTO}.
 */
@Mapper(componentModel = "spring", uses = { CampaignMapper.class, PlayerMapper.class })
public interface ScenarioMapper extends EntityMapper<ScenarioDTO, Scenario> {
    @Mapping(target = "campaign", source = "campaign", qualifiedByName = "name")
    @Mapping(target = "players", source = "players", qualifiedByName = "nameSet")
    ScenarioDTO toDto(Scenario s);

    @Mapping(target = "removePlayers", ignore = true)
    Scenario toEntity(ScenarioDTO scenarioDTO);
}
