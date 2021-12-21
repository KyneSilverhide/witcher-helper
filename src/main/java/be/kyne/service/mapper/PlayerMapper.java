package be.kyne.service.mapper;

import be.kyne.domain.Player;
import be.kyne.service.dto.PlayerDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Player} and its DTO {@link PlayerDTO}.
 */
@Mapper(componentModel = "spring", uses = { CampaignMapper.class })
public interface PlayerMapper extends EntityMapper<PlayerDTO, Player> {
    @Mapping(target = "campaign", source = "campaign", qualifiedByName = "name")
    PlayerDTO toDto(Player s);

    @Named("nameSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Set<PlayerDTO> toDtoNameSet(Set<Player> player);
}
