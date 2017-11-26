package ie.cit.teambravo.cardsec.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ie.cit.teambravo.cardsec.dto.Location;

@Service
public class PanelLocatorServiceImpl implements PanelLocatorService {

	@Value("${panel.locator.endpoint}")
	private String panelLocationEndpoint;

	@Override
	public Location getPanelLocation(String panelId) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(panelLocationEndpoint + "/" + panelId, Location.class);
	}
}
