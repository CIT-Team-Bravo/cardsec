package ie.cit.teambravo.cardsec.validation;

/**
 * API for validation service, deals with validation of incoming card swipes
 */
public interface ValidationService {
	/**
	 * Validates the swipe corresponding to the supplied details
	 * 
	 * @param panelId
	 *            the panel the card was swiped at
	 * @param cardId
	 *            the card id that was swiped
	 * @param allowed
	 *            whether the card is allowed according to the local security information
	 * @return the validation response
	 */
	ValidationResponse validate(String panelId, String cardId, Boolean allowed);
}
