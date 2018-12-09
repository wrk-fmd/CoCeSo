package at.wrk.coceso.entity.helper;

import at.wrk.coceso.entity.enums.Errors;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse implements Serializable {

  private final boolean success;

  private final Map<String, Object> data;

  public RestResponse(final boolean success) {
    this.success = success;
    this.data = null;
  }

  public RestResponse(final boolean success, final RestProperty... properties) {
    this.success = success;
    this.data = Arrays.stream(properties).collect(Collectors.toMap(RestProperty::getKey, RestProperty::getValue));
  }

  public RestResponse(final Errors e) {
    this.success = false;
    this.data = new HashMap<>();
    data.put("error", e.getCode());
    data.put("msg", e.getMessage());
  }

  public RestResponse(final Errors e, final RestProperty... properties) {
    this(false, properties);
    data.put("error", e.getCode());
    data.put("msg", e.getMessage());
  }

  public RestResponse(final BindingResult result) {
    this.success = false;
    this.data = new HashMap<>();
    data.put("error", Errors.Validation.getCode());
    data.put("msg", Errors.Validation.getMessage());
    data.put("errors", result.getAllErrors().stream().collect(Collectors.toMap(ObjectError::getObjectName, ObjectError::getCode)));
  }

  public boolean isSuccess() {
    return success;
  }

  @JsonAnyGetter
  public Map<String, Object> getData() {
    return data;
  }
}
