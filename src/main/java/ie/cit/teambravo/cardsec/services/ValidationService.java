package ie.cit.teambravo.cardsec.services;

public interface ValidationService {
    boolean validate(String panelId, String CardId, Boolean allowed);
}
