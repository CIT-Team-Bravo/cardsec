package ie.cit.teambravo.cardsec;

import ie.cit.teambravo.cardsec.services.ValidationService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationControllerTest {

    @Mock
    private ValidationService validationServiceMock;

    @InjectMocks
    private ValidationController validationController;

    @Test
    public void validationRequest_when_requestIsValid_then_respondWithTrue() {
        String panelId = UUID.randomUUID().toString();
        String cardId = UUID.randomUUID().toString();
        when(validationServiceMock.validate(panelId, cardId, Boolean.TRUE)).thenReturn(true);
        Boolean result = validationController.validationRequest(panelId, cardId, true);

        assertThat(result, Matchers.is(true));
    }

    @Test
    public void validationRequest_when_requestIsNotValid_then_respondWithFalse() {
        String panelId = UUID.randomUUID().toString();
        String cardId = UUID.randomUUID().toString();
        when(validationServiceMock.validate(panelId, cardId, Boolean.FALSE)).thenReturn(false);
        Boolean result = validationController.validationRequest(panelId, cardId, false);


        assertThat(result, Matchers.is(false));
    }

    @Test
    public void validationRequest_when_allowedParamIsNotSpecified_then_respondWithFalse() {
        String panelId = UUID.randomUUID().toString();
        String cardId = UUID.randomUUID().toString();
        when(validationServiceMock.validate(panelId, cardId, null)).thenReturn(false);
        Boolean result = validationController.validationRequest(panelId, cardId, null);


        assertThat(result, Matchers.is(false));
    }

}