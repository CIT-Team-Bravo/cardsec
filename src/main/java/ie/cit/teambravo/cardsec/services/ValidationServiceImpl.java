package ie.cit.teambravo.cardsec.services;

import org.springframework.stereotype.Service;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Override
    public boolean validate(String panelId, String CardId, Boolean allowed) {
        if (Boolean.TRUE. equals(allowed)) {
            return true;
        }

        return false;
    }
}
