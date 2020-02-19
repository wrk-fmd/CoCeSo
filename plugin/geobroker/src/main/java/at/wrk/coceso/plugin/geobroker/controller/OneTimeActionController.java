package at.wrk.coceso.plugin.geobroker.controller;

import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.plugin.geobroker.contract.ota.api.ResultCode;
import at.wrk.coceso.plugin.geobroker.manager.OneTimeActionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(OneTimeActionController.ENDPOINT)
public class OneTimeActionController {
    private static final Logger LOG = LoggerFactory.getLogger(OneTimeActionController.class);

    public static final String ENDPOINT = "/geo/ota";
    private final OneTimeActionManager oneTimeActionManager;

    @Autowired
    public OneTimeActionController(final OneTimeActionManager oneTimeActionManager) {
        this.oneTimeActionManager = oneTimeActionManager;
    }

    @RequestMapping(value = "/{actionId}", produces = "application/json", method = RequestMethod.POST)
    public RestResponse performUnitAction(@PathVariable("actionId") final String actionIdString) {
        UUID actionId;
        try {
            actionId = UUID.fromString(actionIdString.trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            LOG.debug("Received invalid actionId: '{}'", actionIdString);
            return new RestResponse(false, createResultCode(ResultCode.INVALID_ACTION_ID));
        }

        ResultCode resultCode = oneTimeActionManager.executeAction(actionId);

        return resultCode == ResultCode.SUCCESS
                ? new RestResponse(true, createResultCode(ResultCode.SUCCESS))
                : new RestResponse(false, createResultCode(resultCode));
    }

    private RestProperty createResultCode(final ResultCode resultCode) {
        return new RestProperty("resultCode", resultCode);
    }
}
