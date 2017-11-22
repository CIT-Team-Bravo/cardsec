package ie.cit.teambravo.cardsec.services;

import ie.cit.teambravo.cardsec.dto.Location;

public interface PanelLocatorService {
    Location getPanelLocation(String panelId);
}
