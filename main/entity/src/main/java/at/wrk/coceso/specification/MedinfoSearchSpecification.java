package at.wrk.coceso.specification;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Medinfo;
import at.wrk.coceso.entity.Medinfo_;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class MedinfoSearchSpecification extends SearchSpecification<Medinfo> {

  private final Concern concern;

  public MedinfoSearchSpecification(String query, Concern concern) {
    super(query);
    this.concern = concern;
  }

  public MedinfoSearchSpecification(String query, Concern concern, boolean strict) {
    super(query, strict);
    this.concern = concern;
  }

  @Override
  protected Predicate buildKeywordPredicate(String keyword, Root<Medinfo> root, CriteriaBuilder builder) {
    if (strict) {
      return builder.or(
          builder.lower(root.get(Medinfo_.externalId)).in(buildExternal(keyword)),
          builder.equal(builder.lower(root.get(Medinfo_.firstname)), keyword),
          builder.equal(builder.lower(root.get(Medinfo_.lastname)), keyword));
    }

    keyword = "%" + keyword + "%";
    return builder.or(
        builder.like(builder.lower(root.get(Medinfo_.externalId)), keyword),
        builder.like(builder.lower(root.get(Medinfo_.firstname)), keyword),
        builder.like(builder.lower(root.get(Medinfo_.lastname)), keyword));
  }

  @Override
  protected Predicate buildAdditionalPredicates(Root<Medinfo> root, CriteriaBuilder builder) {
    return builder.equal(root.get(Medinfo_.concern), concern);
  }

}
