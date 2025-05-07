package com.wcs.spring_data_jpa_project.service;

import com.wcs.spring_data_jpa_project.dto.UserDeptDTO;
import com.wcs.spring_data_jpa_project.dto.UserSummaryDTO;
import com.wcs.spring_data_jpa_project.model.Department;
import com.wcs.spring_data_jpa_project.model.User;
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
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;


    public void saveUser(User user) {
        entityManager.persist(user);
    }


    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }


    public User updateUser(User user) {
        return entityManager.merge(user);
    }


    public void deleteUser(Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }


    public User assignUserToDepartment(Long userId, Long departmentId) {
        User user = entityManager.find(User.class, userId);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Department department = entityManager.find(Department.class, departmentId);

        if (department == null) {
            throw new RuntimeException("Department not found");
        }

        user.setDepartment(department);

        entityManager.merge(user);

        return user;
    }



    //    TypedQuery<T> is used to when we don't want to Cast like if I want to data only User Type that time we will
//     go for TypedQuery
    public List<User> getAllUsers() {
        String jpql = "SELECT u FROM User u";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        return query.getResultList();
    }


    public List<User> getUsersByCity(String city) {
        String jpql = "SELECT u FROM User u WHERE u.address = :city";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("city", city);
        return query.getResultList();
    }


    //Here we had used createQuery() method
    public Long countUsersByEmailDomain(String domain) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.email LIKE :domain";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("domain", "%" + domain);
        return query.getSingleResult();
    }


    //In this method we have used createNativeQuery() method
    public List<User> getUsersNative() {
        String sql = "SELECT * FROM User";
        Query query = entityManager.createNativeQuery(sql, User.class);
        return query.getResultList();
    }

    public List<User> getUsersByCitySorted(String city) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<User> selectQuery = cb.createQuery(User.class);

        Root<User> root = selectQuery.from(User.class);

        Predicate cityPredicate = cb.equal(root.get("address"), city);

        Order orderByUserName = cb.desc(root.get("userName"));

        selectQuery.select(root)
                .where(cityPredicate)
                .orderBy(orderByUserName);

        return entityManager.createQuery(selectQuery).getResultList();
    }

    @Transactional
    public int updateEmailsByCity(String city, String newEmail) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaUpdate<User> update = cb.createCriteriaUpdate(User.class);

        Root<User> root = update.from(User.class);

        update.set("email", newEmail);

        Predicate cityPredicate = cb.equal(root.get("address"), city);
        update.where(cityPredicate);

        return entityManager.createQuery(update).executeUpdate();
    }

    @Transactional
    public int deleteUsersByCity(String city) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaDelete<User> delete = cb.createCriteriaDelete(User.class);

        Root<User> root = delete.from(User.class);

        Predicate cityPredicate = cb.equal(root.get("address"), city);
        delete.where(cityPredicate);

        return entityManager.createQuery(delete).executeUpdate();
    }

    //Combine and filteration with Predicate
    public List<User> getFilteredUsers(String city, String contact) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        //set<predicate>

        Predicate cityPredicate = cb.equal(root.get("address"), city);
        Predicate contactPredicate = cb.like(root.get("contact"), contact + "%");

        cq.select(root).where(cb.and(cityPredicate, contactPredicate));

        return entityManager.createQuery(cq).getResultList();
    }

    //  Sorting In Ascending  order
    public List<User> getUsersSortedByNameAsc() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.orderBy(cb.asc(root.get("userName")));

        return entityManager.createQuery(cq).getResultList();
    }

    //  Sorting In Descending  Order
    public List<User> getUsersSortedByEmailDesc() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        // Sort by email DESC
        cq.orderBy(cb.desc(root.get("email")));

        return entityManager.createQuery(cq).getResultList();
    }

//  This methods for performing pagination using criteria api

    public List<User> getUsersPaginated(int pageNo, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.select(root); // SELECT * FROM User

        int offset = (pageNo - 1) * pageSize;

        return entityManager.createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public List<User> getUsersWithDepartments() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);

        userRoot.fetch("department", JoinType.INNER);

        // SELECT * FROM User JOIN Department
        // Here we are used distinct(true) because of when we perform OneToMany
        // it can be return multiple duplicate to avoid that value we are used distinct
        cq.select(userRoot).distinct(true);

        return entityManager.createQuery(cq).getResultList();
    }

    public List<User> getUsersByDepartmentName(String deptName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);

        Join<User, Department> deptJoin = userRoot.join("department");

        Predicate deptNamePredicate = cb.equal(deptJoin.get("deptName"), deptName);
        cq.where(deptNamePredicate);

        return entityManager.createQuery(cq).getResultList();
    }

    public List<User> getUsersSortedByDepartmentName() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);

        Join<User, Department> deptJoin = userRoot.join("department");

        cq.orderBy(cb.desc(deptJoin.get("deptName")));
        cq.select(userRoot);

        return entityManager.createQuery(cq).getResultList();
    }

//  DTO Projection
// Interface based Projection

    public List<UserSummaryDTO> getUserSummary() {
        String jpql = "SELECT u.userName AS userName, u.email AS email, u.contact AS contact FROM User u";
        TypedQuery<UserSummaryDTO> query = entityManager.createQuery(jpql, UserSummaryDTO.class);
        return query.getResultList();
    }

// DTO projection based on class

    public List<UserSummaryDTO> getUserSummaryByCity(String city) {
        String jpql = "SELECT new com.wcs.spring_data_jpa_project.dto.UserSummaryDTO(u.userName, u.email) " +
                "FROM User u WHERE u.address = :city";
        return entityManager.createQuery(jpql, UserSummaryDTO.class)
                .setParameter("city", city)
                .getResultList();
    }
// DTO class based projection with join
public List<UserDeptDTO> getUserDepartmentDetails() {
    String jpql = "SELECT new com.wcs.spring_data_jpa_project.dto.UserDeptDTO(u.userName, u.email, d.deptName) " +
            "FROM User u JOIN u.department d";
    return entityManager.createQuery(jpql, UserDeptDTO.class).getResultList();
}









}


//Steps For the Criteria API creation :-
//  Step 1:Here first we will get the object of getCriteriaBuilder()
//  Step 2: Create CriteriaQuery or CriteriaUpdate like interface we can use here for perform operation
//  Step 3: Define root (FROM User) means In SQL we have like From Clause same In this We use Root Clause
//  Step 4: Create Predicate (WHERE u.address = :city)
//  Step 5: Create Order (ORDER BY userName ASC)
//  Step 6: Combine everything
//  Step 7: Execute the query


//jpql and native check which one is better

