package at.wrk.coceso.controller.data;

import at.wrk.coceso.entity.User;
import at.wrk.coceso.entity.helper.JsonViews;
import at.wrk.coceso.entity.helper.PasswordForm;
import at.wrk.coceso.entity.helper.RestProperty;
import at.wrk.coceso.entity.helper.RestResponse;
import at.wrk.coceso.service.UserService;
import at.wrk.coceso.utils.Initializer;
import at.wrk.coceso.validator.PasswordValidator;
import at.wrk.coceso.validator.UserValidator;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("@auth.hasAccessLevel('Edit')")
@RequestMapping("/data/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private PasswordValidator passwordValidator;

    @JsonView(JsonViews.Edit.class)
    @RequestMapping(value = "getAll", produces = "application/json", method = RequestMethod.GET)
    public List<User> getAll() {
        // TODO Access level
        return userService.getAll();
    }

    @Transactional
    @JsonView(JsonViews.UserFull.class)
    @RequestMapping(value = "getFiltered", produces = "application/json", method = RequestMethod.GET)
    public Page<User> getAll(Pageable pageable, @RequestParam(value = "filter", required = false) String filter) {
        // TODO Access level
        return Initializer.init(userService.getAll(pageable, filter), User::getInternalAuthorities);
    }

    @Transactional
    @JsonView(JsonViews.UserFull.class)
    @RequestMapping(value = "get/{id}", produces = "application/json", method = RequestMethod.GET)
    public User getById(@PathVariable("id") int id) {
        // TODO Access level
        return Initializer.init(userService.getById(id), User::getInternalAuthorities);
    }

    @RequestMapping(value = "update", produces = "application/json", method = RequestMethod.POST)
    public RestResponse update(@RequestBody @Validated User editedUser, BindingResult result) {
        userValidator.validate(editedUser, result);
        if (result.hasErrors()) {
            return new RestResponse(result);
        }

        editedUser = userService.update(editedUser);
        return new RestResponse(true, new RestProperty("id", editedUser.getId()));
    }

    @PreAuthorize("@auth.hasAccessLevel('Root')")
    @RequestMapping(value = "setPassword", produces = "application/json", method = RequestMethod.POST)
    public RestResponse setPassword(
            final @RequestBody @Validated PasswordForm form,
            final BindingResult result) {
        passwordValidator.validate(form, result);
        if (result.hasErrors()) {
            return new RestResponse(result);
        }

        return new RestResponse(userService.setPassword(form));
    }

    @RequestMapping(value = "upload", produces = "application/json",
            consumes = "text/csv", method = RequestMethod.POST)
    public RestResponse upload(@RequestBody String body) {
        int ret = userService.importUsers(body);
        if (ret < 0) {
            return new RestResponse(false);
        }
        return new RestResponse(true, new RestProperty("counter", ret));
    }

}
