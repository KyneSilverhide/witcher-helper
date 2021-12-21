package be.kyne.service.criteria;

import be.kyne.domain.enumeration.Profession;
import be.kyne.domain.enumeration.Race;
import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link be.kyne.domain.Player} entity. This class is used
 * in {@link be.kyne.web.rest.PlayerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /players?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PlayerCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Race
     */
    public static class RaceFilter extends Filter<Race> {

        public RaceFilter() {}

        public RaceFilter(RaceFilter filter) {
            super(filter);
        }

        @Override
        public RaceFilter copy() {
            return new RaceFilter(this);
        }
    }

    /**
     * Class for filtering Profession
     */
    public static class ProfessionFilter extends Filter<Profession> {

        public ProfessionFilter() {}

        public ProfessionFilter(ProfessionFilter filter) {
            super(filter);
        }

        @Override
        public ProfessionFilter copy() {
            return new ProfessionFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private RaceFilter race;

    private ProfessionFilter profession;

    private LongFilter campaignId;

    private Boolean distinct;

    public PlayerCriteria() {}

    public PlayerCriteria(PlayerCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.race = other.race == null ? null : other.race.copy();
        this.profession = other.profession == null ? null : other.profession.copy();
        this.campaignId = other.campaignId == null ? null : other.campaignId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PlayerCriteria copy() {
        return new PlayerCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public RaceFilter getRace() {
        return race;
    }

    public RaceFilter race() {
        if (race == null) {
            race = new RaceFilter();
        }
        return race;
    }

    public void setRace(RaceFilter race) {
        this.race = race;
    }

    public ProfessionFilter getProfession() {
        return profession;
    }

    public ProfessionFilter profession() {
        if (profession == null) {
            profession = new ProfessionFilter();
        }
        return profession;
    }

    public void setProfession(ProfessionFilter profession) {
        this.profession = profession;
    }

    public LongFilter getCampaignId() {
        return campaignId;
    }

    public LongFilter campaignId() {
        if (campaignId == null) {
            campaignId = new LongFilter();
        }
        return campaignId;
    }

    public void setCampaignId(LongFilter campaignId) {
        this.campaignId = campaignId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PlayerCriteria that = (PlayerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(race, that.race) &&
            Objects.equals(profession, that.profession) &&
            Objects.equals(campaignId, that.campaignId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, race, profession, campaignId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlayerCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (race != null ? "race=" + race + ", " : "") +
            (profession != null ? "profession=" + profession + ", " : "") +
            (campaignId != null ? "campaignId=" + campaignId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
