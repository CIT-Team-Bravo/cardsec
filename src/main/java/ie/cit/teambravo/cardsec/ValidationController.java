package ie.cit.teambravo.cardsec;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(value = "api/panels", produces = "application/json; charset=UTF-8")
public class ValidationController {

	@ApiOperation(value = "Validate panel swipe request", code = 201, response = Boolean.class, httpMethod = "POST")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Validate panel swipe request", response = Boolean.class),
	})
	@RequestMapping(value = "/request", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Boolean validationRequest() {
		return false;
	}

}
