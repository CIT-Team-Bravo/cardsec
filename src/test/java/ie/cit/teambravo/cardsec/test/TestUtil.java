package ie.cit.teambravo.cardsec.test;

import java.util.UUID;

import ie.cit.teambravo.cardsec.events.Event;
import ie.cit.teambravo.cardsec.location.Coordinates;
import ie.cit.teambravo.cardsec.location.Location;

public class TestUtil {
	public static Event generateTestEvent() {
		Event anEventWhereTheDataIsNotImportant = new Event();
		anEventWhereTheDataIsNotImportant.setPanelId(UUID.randomUUID().toString());
		anEventWhereTheDataIsNotImportant.setCardId(UUID.randomUUID().toString());
		anEventWhereTheDataIsNotImportant.setTimestamp(System.currentTimeMillis() / 1000);
		anEventWhereTheDataIsNotImportant.setAccessAllowed(false);

		Location location = new Location();
		location.setAltitude(100);
		location.setRelativeLocation("Someplace, TX, USA");

		Coordinates coordinates = new Coordinates();
		coordinates.setLatitude(10.1);
		coordinates.setLongitude(1.10);

		location.setCoordinates(coordinates);
		location.setAltitude(100);

		anEventWhereTheDataIsNotImportant.setLocation(location);

		return anEventWhereTheDataIsNotImportant;
	}
}
