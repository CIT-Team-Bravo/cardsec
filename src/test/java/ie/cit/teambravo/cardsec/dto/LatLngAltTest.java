package ie.cit.teambravo.cardsec.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class LatLngAltTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(LatLngAlt.class).verify();
    }
}
