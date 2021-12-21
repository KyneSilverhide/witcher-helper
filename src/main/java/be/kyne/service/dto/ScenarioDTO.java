package be.kyne.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link be.kyne.domain.Scenario} entity.
 */
public class ScenarioDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @Lob
    private String description;

    @NotNull
    private String mapCoords;

    private LocalDate date;

    @NotNull
    private String mapIcon;

    private CampaignDTO campaign;

    private Set<PlayerDTO> players = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMapCoords() {
        return mapCoords;
    }

    public void setMapCoords(String mapCoords) {
        this.mapCoords = mapCoords;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMapIcon() {
        return mapIcon;
    }

    public void setMapIcon(String mapIcon) {
        this.mapIcon = mapIcon;
    }

    public CampaignDTO getCampaign() {
        return campaign;
    }

    public void setCampaign(CampaignDTO campaign) {
        this.campaign = campaign;
    }

    public Set<PlayerDTO> getPlayers() {
        return players;
    }

    public void setPlayers(Set<PlayerDTO> players) {
        this.players = players;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScenarioDTO)) {
            return false;
        }

        ScenarioDTO scenarioDTO = (ScenarioDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, scenarioDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScenarioDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", mapCoords='" + getMapCoords() + "'" +
            ", date='" + getDate() + "'" +
            ", mapIcon='" + getMapIcon() + "'" +
            ", campaign=" + getCampaign() +
            ", players=" + getPlayers() +
            "}";
    }
}
