package com.wcs.spring_data_jpa_project.service.core;

import com.wcs.spring_data_jpa_project.model.Department;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DepartmentService {

    @PersistenceContext
    private EntityManager entityManager;


    public void saveDepartment(Department department) {
        entityManager.persist(department);
    }


    public Department getDepartmentById(Long id) {
        return entityManager.find(Department.class, id);
    }


    public Department updateDepartment(Department department) {
        return entityManager.merge(department);
    }


    public void deleteDepartment(Long id) {
        Department dept = entityManager.find(Department.class, id);
        if (dept != null) {
            entityManager.remove(dept);
        }
    }

    public List<Department> getAllDepartments() {
        String jpql = "SELECT d FROM Department d";
        TypedQuery<Department> query = entityManager.createQuery(jpql, Department.class);
        return query.getResultList();
    }

    public Department getDepartmentByName(String name) {
        String jpql = "SELECT d FROM Department d WHERE d.name = :name";
        TypedQuery<Department> query = entityManager.createQuery(jpql, Department.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    public List<Department> getDepartmentsNative() {
        String sql = "SELECT * FROM Department";
        Query query = entityManager.createNativeQuery(sql, Department.class);
        return query.getResultList();
    }

    public Long countByName(String name) {
        String jpql = "SELECT COUNT(d) FROM Department d WHERE d.name = :name";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    public List<Department> getDepartmentsByNameCriteria(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Department> cq = cb.createQuery(Department.class);
        Root<Department> root = cq.from(Department.class);

        Predicate namePredicate = cb.equal(root.get("name"), name);
        cq.select(root).where(namePredicate);

        return entityManager.createQuery(cq).getResultList();
    }

    public List<Department> getDepartmentsSortedByNameAsc() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Department> cq = cb.createQuery(Department.class);
        Root<Department> root = cq.from(Department.class);
        cq.orderBy(cb.asc(root.get("name")));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Department> getDepartmentsPaginated(int pageNo, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Department> cq = cb.createQuery(Department.class);
        Root<Department> root = cq.from(Department.class);
        cq.select(root);

        int offset = (pageNo - 1) * pageSize;
        return entityManager.createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public int updateDepartmentName(Long id, String newName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Department> update = cb.createCriteriaUpdate(Department.class);
        Root<Department> root = update.from(Department.class);

        update.set("name", newName)
                .where(cb.equal(root.get("id"), id));

        return entityManager.createQuery(update).executeUpdate();
    }

    public int deleteDepartmentByName(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Department> delete = cb.createCriteriaDelete(Department.class);
        Root<Department> root = delete.from(Department.class);

        delete.where(cb.equal(root.get("name"), name));
        return entityManager.createQuery(delete).executeUpdate();
    }
}

// Topics Covered

// JPQL: Get department by name
// JPQL: Get all departments
// Native Query: Get all departments
// JPQL: Count departments by name
// Criteria: Filter by name
// Criteria: Sort by name ASC
// Criteria: Pagination
// Criteria: Update name by ID
// Criteria: Delete by name


