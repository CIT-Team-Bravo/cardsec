package ie.cit.teambravo.cardsec.validation;

public interface ValidationService {
	ValidationResponse validate(String panelId, String cardId, Boolean allowed);
}
