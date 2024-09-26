package com.demo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;

import com.demo.dto.PageRequestDto;
import com.demo.dto.PageResponseDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class QueryUtil<T> {
	
	private final EntityManager entityManager;
	
	public QueryUtil(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
	
	public PageResponseDto<T> getPaginatedData(PageRequestDto pageRequestDto, Class<T> clazz) {
       validatePaginationParams(pageRequestDto.getSize(), pageRequestDto.getOffset());
       
       Session session = entityManager.unwrap(Session.class);
    // Initialize CriteriaBuilder
       HibernateCriteriaBuilder hcb = session.getCriteriaBuilder();
       
    // Create a HibernateCriteriaQuery for fetching results
       JpaCriteriaQuery<T> criteriaQuery = hcb.createQuery(clazz);
       Root<T> root = criteriaQuery.from(clazz);  // Equivalent to SELECT * FROM <Entity>
       
    // Apply filters
       List<Predicate> predicates = new ArrayList<>();
       if (pageRequestDto.getFilters() != null && !pageRequestDto.getFilters().isEmpty()) {
           for (Map.Entry<String, String> filter : pageRequestDto.getFilters().entrySet()) {
               String field = filter.getKey();
               String value = filter.getValue();

               // "Contains" filter: Apply LIKE condition
               predicates.add(hcb.like(root.get(field), "%" + value + "%"));
           }
       }
       
    // Add the predicates to the query
       if (!predicates.isEmpty()) {
           criteriaQuery.where(hcb.and(predicates.toArray(new Predicate[0])));
       }
       
    // Apply sorting
       if (pageRequestDto.getSortField() != null && !pageRequestDto.getSortField().isEmpty()) {
           Order order = getSortOrder(hcb, root, pageRequestDto.getSortField(), pageRequestDto.getSortDirection());
           criteriaQuery.orderBy(order);
       }
       
    // Create a paginated query
       Query<T> query = session.createQuery(criteriaQuery);
       query.setFirstResult(pageRequestDto.getOffset());  // Set pagination offset
       query.setMaxResults(pageRequestDto.getSize());     // Set pagination size
       
    // Fetch the paginated result
       List<T> result = query.list();
       
    // Fetch the total record count
       Long totalRecords = getTotalRecordCount(hcb, clazz, session, pageRequestDto.getFilters());

       // Calculate total pages
       int totalPages = (int) Math.ceil((double) totalRecords / pageRequestDto.getSize());
       int currentPage = pageRequestDto.getOffset() / pageRequestDto.getSize();

       // Return the paginated result wrapped in a DTO
       return new PageResponseDto<>(result, totalRecords, totalPages, currentPage);

    }
	
	private Long getTotalRecordCount(HibernateCriteriaBuilder hcb, Class<T> clazz, Session session, Map<String, String> filters) {
        // Create a count query
		JpaCriteriaQuery<Long> countQuery = hcb.createQuery(Long.class);
		Root<T> root = countQuery.from(clazz);
        countQuery.select(hcb.count(root));  // Equivalent to SELECT COUNT(*)
        
     // Apply filters to the count query as well
        List<Predicate> predicates = new ArrayList<>();
        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, String> filter : filters.entrySet()) {
                String field = filter.getKey();
                String value = filter.getValue();
                predicates.add(hcb.like(root.get(field), "%" + value + "%"));
            }
        }
        
        if (!predicates.isEmpty()) {
            countQuery.where(hcb.and(predicates.toArray(new Predicate[0])));
        }
        return session.createQuery(countQuery).getSingleResult();
    }
	
	private Order getSortOrder(HibernateCriteriaBuilder hcb, Root<T> root, String sortField, String sortDirection) {
        if ("asc".equalsIgnoreCase(sortDirection)) {
            return hcb.asc(root.get(sortField));  // Ascending order
        } else {
            return hcb.desc(root.get(sortField));  // Descending order
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
