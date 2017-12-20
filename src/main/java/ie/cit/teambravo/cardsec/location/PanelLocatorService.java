package ie.cit.teambravo.cardsec.location;

/**
 * API for the location package, deals with the locations of the wipe panels
 */
public interface PanelLocatorService {
	/**
	 * Get the location of the specified panel
	 * 
	 * @param panelId
	 *            the id of the panel
	 * @return the location
	 */
	Location getPanelLocation(String panelId);
}
