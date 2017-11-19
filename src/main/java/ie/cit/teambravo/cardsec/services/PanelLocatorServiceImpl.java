package ie.cit.teambravo.cardsec.services;

import ie.cit.teambravo.cardsec.dto.LocationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PanelLocatorServiceImpl implements PanelLocatorService {

    @Value("${panel.locator.endpoint}")
    private String PANEL_LOCATION_ENDPOINT;

    @Override
    public LocationDto getPanelLocation(String panelId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(PANEL_LOCATION_ENDPOINT + "/" + panelId, LocationDto.class);
    }
}
