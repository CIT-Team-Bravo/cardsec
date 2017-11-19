package ie.cit.teambravo.cardsec.services;

import ie.cit.teambravo.cardsec.dto.EventDto;
import ie.cit.teambravo.cardsec.dto.LocationDto;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceImplTest {
    @Mock
    private EventService eventServiceMock;
    @Mock
    private PanelLocatorService panelLocatorServiceMock;
    @InjectMocks
    private ValidationService validationService = new ValidationServiceImpl();

    @Test
    public void validate_when_requestIsValid_then_respondWithTrue() {
        // Arrange
        String panelId = UUID.randomUUID().toString();
        String cardId = UUID.randomUUID().toString();
        EventDto eventToBeSaved = new EventDto();
        eventToBeSaved.setPanelId(panelId);
        eventToBeSaved.setCardId(cardId);
        when(eventServiceMock.saveEvent(eventToBeSaved)).thenReturn(new EventDto());
        when(panelLocatorServiceMock.getPanelLocation(panelId)).thenReturn(new LocationDto());

        // Act
        Boolean result = validationService.validate(panelId, cardId, true);

        // Assert & Verify
        assertThat(result, Matchers.is(true));
        verify(eventServiceMock).saveEvent(any(eventToBeSaved.getClass()));
    }

    @Test
    public void validate_when_requestIsNotValid_then_respondWithTrue() {
        // Arrange
        String panelId = UUID.randomUUID().toString();
        String cardId = UUID.randomUUID().toString();
        EventDto eventToBeSaved = new EventDto();
        eventToBeSaved.setPanelId(panelId);
        eventToBeSaved.setCardId(cardId);
        when(eventServiceMock.saveEvent(eventToBeSaved)).thenReturn(new EventDto());

        // Act
        Boolean result = validationService.validate(panelId, cardId, false);

        // Assert & Verify
        assertThat(result, Matchers.is(false));
        verify(eventServiceMock).saveEvent(any(eventToBeSaved.getClass()));
    }

}