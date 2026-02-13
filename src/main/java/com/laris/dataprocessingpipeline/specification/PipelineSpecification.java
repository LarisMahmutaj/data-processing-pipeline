package com.laris.dataprocessingpipeline.specification;

import com.laris.dataprocessingpipeline.domain.Pipeline;
import com.laris.dataprocessingpipeline.domain.PipelineNode;
import com.laris.dataprocessingpipeline.domain.PipelinePermission;
import com.laris.dataprocessingpipeline.dto.request.PipelineSearchRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PipelineSpecification {

    public static Specification<Pipeline> withFilters(PipelineSearchRequest search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search.getName() != null && !search.getName().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + search.getName().toLowerCase() + "%"
                ));
            }

            if (search.getOwnerId() != null) {
                predicates.add(cb.equal(root.get("owner").get("id"), search.getOwnerId()));
            }

            if (search.getErrorHandlingStrategy() != null) {
                predicates.add(cb.equal(
                        root.get("errorHandlingStrategy"),
                        search.getErrorHandlingStrategy()
                ));
            }

            if (search.getNodeType() != null) {
                Join<Pipeline, PipelineNode> nodesJoin = root.join("nodes");
                predicates.add(cb.equal(nodesJoin.get("nodeType"), search.getNodeType()));
            }

            if (search.getRoleIds() != null && !search.getRoleIds().isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<PipelinePermission> permRoot = subquery.from(PipelinePermission.class);
                subquery.select(permRoot.get("pipeline").get("id"))
                        .where(permRoot.get("role").get("id").in(search.getRoleIds()));

                predicates.add(root.get("id").in(subquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
