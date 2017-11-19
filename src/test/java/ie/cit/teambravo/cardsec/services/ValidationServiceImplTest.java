package ie.cit.teambravo.cardsec.services;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceImplTest {

    private ValidationService validationService = new ValidationServiceImpl();

    @Test
    public void validate_when_requestIsValid_then_respondWithTrue() {
        String panelId = UUID.randomUUID().toString();
        String cardId = UUID.randomUUID().toString();
        Boolean result = validationService.validate(panelId, cardId, true);

        assertThat(result, Matchers.is(true));
    }

    @Test
    public void validate_when_requestIsNotValid_then_respondWithTrue() {
        String panelId = UUID.randomUUID().toString();
        String cardId = UUID.randomUUID().toString();
        Boolean result = validationService.validate(panelId, cardId, false);

        assertThat(result, Matchers.is(false));
    }

}