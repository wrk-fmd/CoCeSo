package at.wrk.coceso.entity.helper;

import at.wrk.coceso.entity.enums.Errors;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse implements Serializable {

  private final boolean success;

  private final Map<String, Object> data;

  public RestResponse(boolean success) {
    this.success = success;
    this.data = null;
  }

  public RestResponse(boolean success, RestProperty... properties) {
    this.success = success;
    this.data = Arrays.asList(properties).stream().collect(Collectors.toMap(RestProperty::getKey, RestProperty::getValue));
  }

  public RestResponse(Errors e) {
    this.success = false;
    this.data = new HashMap<>();
    data.put("error", e.getCode());
    data.put("msg", e.getMessage());
  }

  public RestResponse(Errors e, RestProperty... properties) {
    this(false, properties);
    data.put("error", e.getCode());
    data.put("msg", e.getMessage());
  }

  public RestResponse(BindingResult result) {
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
