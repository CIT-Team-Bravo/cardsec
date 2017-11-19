package ie.cit.teambravo.cardsec.services;

import ie.cit.teambravo.cardsec.dto.LocationDto;

public interface PanelLocatorService {
    LocationDto getPanelLocation(String panelId);
}
