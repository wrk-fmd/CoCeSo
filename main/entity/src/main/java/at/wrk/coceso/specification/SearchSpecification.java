package at.wrk.coceso.specification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class SearchSpecification<T> implements Specification<T> {

  private final String[] keywords;
  protected final boolean strict;

  //Prefixes and suffixes for the number
  private static final String[] prefixes = new String[]{"", "m-", "h-", "s-", "j-", "k-"};
  private static final String[] suffixes = new String[]{"", "a", "b", "c", "d"};

  public SearchSpecification(String query) {
    this(query, !query.contains("*"));
  }

  public SearchSpecification(String query, boolean strict) {
    this.keywords = query.replace('*', ' ').split(" ");
    this.strict = strict;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder builder) {
    Map<String, Predicate> queries = new HashMap<>();
    for (String keyword : keywords) {
      if (StringUtils.isBlank(keyword)) {
        continue;
      }
      keyword = keyword.toLowerCase();
      queries.put(keyword, buildKeywordPredicate(keyword, root, builder));
    }
    return builder.and(buildAdditionalPredicates(root, builder), builder.and(queries.values().toArray(new Predicate[0])));
  }

  protected Integer buildId(String keyword) {
    try {
      return Integer.parseInt(keyword);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  protected Set<String> buildExternal(String keyword) {
    Set<String> ret = new HashSet<>();
    for (String prefix : prefixes) {
      for (String suffix : suffixes) {
        ret.add(prefix + keyword + suffix);
      }
    }
    return ret;
  }

  protected abstract Predicate buildKeywordPredicate(String keyword, Root<T> root, CriteriaBuilder builder);

  protected abstract Predicate buildAdditionalPredicates(Root<T> root, CriteriaBuilder builder);
}
