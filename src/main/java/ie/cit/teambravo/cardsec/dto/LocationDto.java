package ie.cit.teambravo.cardsec.dto;

public class LocationDto {
    private CoordinatesDto coordinatesDto;
    private String altitude;
    private String relativeLocation;

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
