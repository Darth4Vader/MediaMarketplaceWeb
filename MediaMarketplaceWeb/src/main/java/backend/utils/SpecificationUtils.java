package backend.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class SpecificationUtils {
	
	public static Predicate filterByName(CriteriaBuilder cb, CriteriaQuery<?> query, String name, Path<Object> nameAttribute) {
        if(name != null) {
            Expression<Integer> differenceName = cb.function("levenshtein_ratio", Integer.class, nameAttribute, cb.literal(name));
            // You can compare if the difference is greater than a threshold value, e.g., 3
            
            // order by closest matching
            query.orderBy(cb.asc(differenceName), cb.asc(nameAttribute));
            return cb.lessThan(differenceName, 70); // Adjust the threshold as needed
        }
        return null;
	}

}
