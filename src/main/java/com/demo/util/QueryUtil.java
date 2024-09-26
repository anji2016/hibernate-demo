package com.demo.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.demo.dto.PageRequestDto;
import com.demo.dto.PageResponseDto;

public class QueryUtil<T> {

    private final EntityManager entityManager;

    public QueryUtil(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public PageResponseDto<T> getPaginatedData(PageRequestDto pageRequestDto, Class<T> clazz) {
        validatePaginationParams(pageRequestDto.getSize(), pageRequestDto.getOffset());

        // Initialize CriteriaBuilder
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Create a CriteriaQuery for fetching results
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);  // Equivalent to SELECT * FROM <Entity>

        // Apply filters
        List<Predicate> predicates = new ArrayList<>();
        if (pageRequestDto.getFilters() != null && !pageRequestDto.getFilters().isEmpty()) {
            for (Map.Entry<String, String> filter : pageRequestDto.getFilters().entrySet()) {
                String field = filter.getKey();
                String value = filter.getValue();

                // "Contains" filter: Apply LIKE condition
                predicates.add(cb.like(root.get(field), "%" + value + "%"));
            }
        }

        // Add the predicates to the query
        if (!predicates.isEmpty()) {
            criteriaQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // Apply sorting
        if (pageRequestDto.getSortField() != null && !pageRequestDto.getSortField().isEmpty()) {
            Order order = getSortOrder(cb, root, pageRequestDto.getSortField(), pageRequestDto.getSortDirection());
            criteriaQuery.orderBy(order);
        }

        // Create a paginated query
        Query query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(pageRequestDto.getOffset());  // Set pagination offset
        query.setMaxResults(pageRequestDto.getSize());     // Set pagination size

        // Fetch the paginated result
        List<T> result = query.getResultList();

        // Fetch the total record count
        Long totalRecords = getTotalRecordCount(cb, clazz, pageRequestDto.getFilters());

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalRecords / pageRequestDto.getSize());
        int currentPage = pageRequestDto.getOffset() / pageRequestDto.getSize();

        // Return the paginated result wrapped in a DTO
        return new PageResponseDto<>(result, totalRecords, totalPages, currentPage);
    }

    private Long getTotalRecordCount(CriteriaBuilder cb, Class<T> clazz, Map<String, String> filters) {
        // Create a count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> root = countQuery.from(clazz);
        countQuery.select(cb.count(root));  // Equivalent to SELECT COUNT(*)

        // Apply filters to the count query as well
        List<Predicate> predicates = new ArrayList<>();
        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, String> filter : filters.entrySet()) {
                String field = filter.getKey();
                String value = filter.getValue();
                predicates.add(cb.like(root.get(field), "%" + value + "%"));
            }
        }

        if (!predicates.isEmpty()) {
            countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private Order getSortOrder(CriteriaBuilder cb, Root<T> root, String sortField, String sortDirection) {
        if ("asc".equalsIgnoreCase(sortDirection)) {
            return cb.asc(root.get(sortField));  // Ascending order
        } else {
            return cb.desc(root.get(sortField));  // Descending order
        }
    }

    private void validatePaginationParams(int size, int offset) {
        if (size < 1) {
            throw new IllegalArgumentException("Page size must be at least 1");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be negative");
        }
    }
}
