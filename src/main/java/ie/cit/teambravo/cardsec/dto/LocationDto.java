package ie.cit.teambravo.cardsec.dto;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class LocationDto {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
    @OneToOne
    private CoordinatesDto coordinatesDto;
    private String altitude;
    private String relativeLocation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CoordinatesDto getCoordinatesDto() {
        return coordinatesDto;
    }

    public void setCoordinatesDto(CoordinatesDto coordinatesDto) {
        this.coordinatesDto = coordinatesDto;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getRelativeLocation() {
        return relativeLocation;
    }

    public void setRelativeLocation(String relativeLocation) {
        this.relativeLocation = relativeLocation;
    }
}
