//package at.wrk.coceso.validator.impl;
//
//import at.wrk.coceso.entity.Concern;
//import at.wrk.coceso.service.ConcernService;
//import at.wrk.coceso.validator.ConcernValidator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.Errors;
//
//@Component
//class ConcernValidatorImpl implements ConcernValidator {
//
//  @Autowired
//  ConcernService concernService;
//
//  @Override
//  public boolean supports(Class<?> type) {
//    return Concern.class.equals(type);
//  }
//
//  @Override
//  public void validate(Object o, Errors errors) {
//    Concern c = (Concern) o;
//
//    // Only open concerns can be updated
//    if (c.getId() != null) {
//      Concern old = concernService.getById(c.getId());
//      if (old == null) {
//        errors.reject("concern.missing");
//        return;
//      } else if (old.isClosed()) {
//        errors.reject("concern.closed");
//        return;
//      }
//    }
//
//    Concern existing = concernService.getByName(c.getName());
//    if (existing != null && !c.equals(existing)) {
//      errors.rejectValue("name", "concern.name.exists");
//    }
//  }
//
//}
