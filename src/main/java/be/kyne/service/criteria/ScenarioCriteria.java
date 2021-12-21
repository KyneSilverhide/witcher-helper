package be.kyne.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link be.kyne.domain.Scenario} entity. This class is used
 * in {@link be.kyne.web.rest.ScenarioResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /scenarios?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ScenarioCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter mapCoords;

    private LocalDateFilter date;

    private StringFilter mapIcon;

    private LongFilter campaignId;

    private LongFilter playersId;

    private Boolean distinct;

    public ScenarioCriteria() {}

    public ScenarioCriteria(ScenarioCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.mapCoords = other.mapCoords == null ? null : other.mapCoords.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.mapIcon = other.mapIcon == null ? null : other.mapIcon.copy();
        this.campaignId = other.campaignId == null ? null : other.campaignId.copy();
        this.playersId = other.playersId == null ? null : other.playersId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ScenarioCriteria copy() {
        return new ScenarioCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getMapCoords() {
        return mapCoords;
    }

    public StringFilter mapCoords() {
        if (mapCoords == null) {
            mapCoords = new StringFilter();
        }
        return mapCoords;
    }

    public void setMapCoords(StringFilter mapCoords) {
        this.mapCoords = mapCoords;
    }

    public LocalDateFilter getDate() {
        return date;
    }

    public LocalDateFilter date() {
        if (date == null) {
            date = new LocalDateFilter();
        }
        return date;
    }

    public void setDate(LocalDateFilter date) {
        this.date = date;
    }

    public StringFilter getMapIcon() {
        return mapIcon;
    }

    public StringFilter mapIcon() {
        if (mapIcon == null) {
            mapIcon = new StringFilter();
        }
        return mapIcon;
    }

    public void setMapIcon(StringFilter mapIcon) {
        this.mapIcon = mapIcon;
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

    public LongFilter getPlayersId() {
        return playersId;
    }

    public LongFilter playersId() {
        if (playersId == null) {
            playersId = new LongFilter();
        }
        return playersId;
    }

    public void setPlayersId(LongFilter playersId) {
        this.playersId = playersId;
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
        final ScenarioCriteria that = (ScenarioCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(mapCoords, that.mapCoords) &&
            Objects.equals(date, that.date) &&
            Objects.equals(mapIcon, that.mapIcon) &&
            Objects.equals(campaignId, that.campaignId) &&
            Objects.equals(playersId, that.playersId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, mapCoords, date, mapIcon, campaignId, playersId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScenarioCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (mapCoords != null ? "mapCoords=" + mapCoords + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (mapIcon != null ? "mapIcon=" + mapIcon + ", " : "") +
            (campaignId != null ? "campaignId=" + campaignId + ", " : "") +
            (playersId != null ? "playersId=" + playersId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
