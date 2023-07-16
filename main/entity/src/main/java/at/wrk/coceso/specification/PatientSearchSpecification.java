package at.wrk.coceso.specification;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.Patient_;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class PatientSearchSpecification extends SearchSpecification<Patient> {

  private final Concern concern;
  private final boolean showDone;

  public PatientSearchSpecification(String query, Concern concern, boolean showDone) {
    super(query);
    this.concern = concern;
    this.showDone = showDone;
  }

  public PatientSearchSpecification(String query, Concern concern, boolean showDone, boolean strict) {
    super(query, strict);
    this.concern = concern;
    this.showDone = showDone;
  }

  @Override
  protected Predicate buildKeywordPredicate(String keyword, Root<Patient> root, CriteriaBuilder builder) {
    if (strict) {
      return builder.or(
          builder.equal(root.get(Patient_.id), buildId(keyword)),
          builder.lower(root.get(Patient_.externalId)).in(buildExternal(keyword)),
          builder.equal(builder.lower(root.get(Patient_.firstname)), keyword),
          builder.equal(builder.lower(root.get(Patient_.lastname)), keyword));
    }

    Integer id = buildId(keyword);
    keyword = "%" + keyword + "%";
    return builder.or(
        builder.equal(root.get(Patient_.id), id),
        builder.like(builder.lower(root.get(Patient_.externalId)), keyword),
        builder.like(builder.lower(root.get(Patient_.firstname)), keyword),
        builder.like(builder.lower(root.get(Patient_.lastname)), keyword));
  }

  @Override
  protected Predicate buildAdditionalPredicates(Root<Patient> root, CriteriaBuilder builder) {
    Predicate pred = builder.equal(root.get(Patient_.concern), concern);
    if (!showDone) {
      pred = builder.and(pred, builder.isFalse(root.get(Patient_.done)));
    }
    return pred;
  }

}
