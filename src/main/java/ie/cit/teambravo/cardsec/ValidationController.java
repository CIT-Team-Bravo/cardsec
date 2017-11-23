package ie.cit.teambravo.cardsec;

import ie.cit.teambravo.cardsec.services.ValidationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "api/panels", produces = "application/json; charset=UTF-8")
public class ValidationController {

	@Autowired
	private ValidationService validationService;

	@ApiOperation(value = "Validate panel swipe request", code = 201, response = Boolean.class, httpMethod = "PUT")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Validate panel swipe request", response = Boolean.class),
	})
	@RequestMapping(value = "/request", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public Boolean validationRequest(@RequestParam String panelId, @RequestParam String cardId,
			@RequestParam Boolean allowed) {
		return validationService.validate(panelId, cardId, allowed);
	}

}
