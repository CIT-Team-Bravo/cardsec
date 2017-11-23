package ie.cit.teambravo.cardsec.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ie.cit.teambravo.cardsec.alerts.AlertService;
import ie.cit.teambravo.cardsec.validation.ValidationServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceImplTest {
	private EventService eventServiceMock;
	private PanelLocatorService panelLocatorServiceMock;
	private AlertService alertServiceMock;
	private DurationService durationServiceMock;
	private ValidationServiceImpl validationServiceImpl;

	@Before
	public void setup() {
		eventServiceMock = Mockito.mock(EventService.class);
		panelLocatorServiceMock = Mockito.mock(PanelLocatorService.class);
		alertServiceMock = Mockito.mock(AlertService.class);
		durationServiceMock = Mockito.mock(DurationService.class);

		validationServiceImpl = new ValidationServiceImpl(eventServiceMock, panelLocatorServiceMock, alertServiceMock,
				durationServiceMock);
	}

	@Test
	public void test() {

	}
}
