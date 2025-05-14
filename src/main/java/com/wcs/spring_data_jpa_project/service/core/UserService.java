package com.wcs.spring_data_jpa_project.service.core;

import com.wcs.spring_data_jpa_project.dto.LoginRequest;
import com.wcs.spring_data_jpa_project.dto.RegisterRequest;
import com.wcs.spring_data_jpa_project.dto.UserDeptDTO;
import com.wcs.spring_data_jpa_project.dto.UserSummaryDTO;
import com.wcs.spring_data_jpa_project.exception.*;
//import com.wcs.spring_data_jpa_project.jwt.JwtService;
import com.wcs.spring_data_jpa_project.jwt.JwtService;
import com.wcs.spring_data_jpa_project.model.Department;
import com.wcs.spring_data_jpa_project.model.Role;
import com.wcs.spring_data_jpa_project.model.User;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;


    public User registerUser(RegisterRequest request) {
        if (request == null || request.getEmail() == null) {
            log.error("Invalid registration request");
            throw new InvalidInputException("Registration request or email cannot be null");
        }

        // Check if the email already exists in the system
        String emailCheckQuery = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
        Long count = entityManager.createQuery(emailCheckQuery, Long.class)
                .setParameter("email", request.getEmail())
                .getSingleResult();

        if (count != null && count > 0) {
            log.error("Registration failed - Email already in use: {}", request.getEmail());
            throw new DuplicateResourceException("Email already registered");
        }

        // Create a new user
        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setContact(request.getContact());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Assign role based on the provided role in request (default to "ROLE_USER" if not provided)
        String roleName = request.getRole() != null ? request.getRole() : "ROLE_USER";

        // Retrieve the role by name, or create it if it doesn't exist
        Role role = entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :roleName", Role.class)
                .setParameter("roleName", roleName)
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    // Create new role if not found
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    entityManager.persist(newRole);
                    return newRole;
                });

        // Set the role for the user
        user.setRoles(Collections.singleton(role));

        // Persist the user in the database
        entityManager.persist(user);
        log.info("User registered successfully: {}", user.getEmail());

        // Send a welcome email
        emailService.sendEmail(
                user.getEmail(),
                "Welcome to Our App!",
                "Hi " + user.getUserName() + ",\n\nThanks for registering with us. We're glad to have you!"
        );

        return user;
    }



    public String loginUser(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            log.error("Invalid login request: {}", request);
            throw new InvalidInputException("Email and password must not be null");
        }

        try {
            String jpql = "SELECT u FROM User u WHERE u.email = :email";
            TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
            query.setParameter("email", request.getEmail());
            User user = query.getSingleResult();

            boolean matched = passwordEncoder.matches(request.getPassword(), user.getPassword());
            if (matched) {
                log.info("Login successful for: {}", request.getEmail());

                // ✅ Generate JWT token
                String token = jwtService.generateToken(user.getEmail());
                return token;
            } else {
                log.warn("Login failed - Incorrect password for: {}", request.getEmail());
                throw new InvalidCredentialsException("Invalid email or password");
            }
        } catch (NoResultException e) {
            log.error("Login failed - User not found: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }
//    Fuzzy Search is not done in this method means for searching we have to enter full word
    public List<User> dynamicSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            log.error("Search keyword is invalid: {}", keyword);
            throw new InvalidInputException("Search keyword must not be null or empty");
        }

        log.debug("Performing dynamic search with keyword: {}", keyword);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        Predicate userNamePredicate = cb.like(cb.lower(root.get("userName")), "%" + keyword.toLowerCase() + "%");
        Predicate emailPredicate = cb.like(cb.lower(root.get("email")), "%" + keyword.toLowerCase() + "%");
        Predicate addressPredicate = cb.like(cb.lower(root.get("address")), "%" + keyword.toLowerCase() + "%");

        cq.where(cb.or(userNamePredicate, emailPredicate, addressPredicate));

        return entityManager.createQuery(cq).getResultList();
    }

    public void saveUser(User user) {
        if (user == null) {
            log.error("Attempt to save a null user");
            throw new InvalidInputException("User data must not be null");
        }
        log.info("Saving user: {}", user.getUserName());
        entityManager.persist(user);
    }

    public User getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        User user = entityManager.find(User.class, id);
        if (user == null) {
            log.error("User with ID {} not found", id);
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
        return user;
    }

    public User updateUser(User user) {
        if (user == null || user.getId() == null) {
            log.error("Invalid user input for update: {}", user);
            throw new InvalidInputException("User or ID must not be null for update");
        }

        User existingUser = entityManager.find(User.class, user.getId());
        if (existingUser == null) {
            log.error("User with ID {} not found for update", user.getId());
            throw new UserNotFoundException("Cannot update: User with ID " + user.getId() + " does not exist");
        }

        log.info("Updating user: {}", user.getUserName());
        return entityManager.merge(user);
    }

    public void deleteUser(Long id) {
        log.warn("Attempting to delete user with ID: {}", id);
        User user = entityManager.find(User.class, id);
        if (user == null) {
            log.error("User with ID {} not found for deletion", id);
            throw new UserNotFoundException("User with ID " + id + " not found for deletion");
        }
        entityManager.remove(user);
        log.info("User with ID {} deleted successfully", id);
    }

    public User assignUserToDepartment(Long userId, Long departmentId) {
        log.info("Assigning user {} to department {}", userId, departmentId);
        User user = entityManager.find(User.class, userId);
        if (user == null) {
            log.error("User with ID  {} not found", userId);
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        Department department = entityManager.find(Department.class, departmentId);
        if (department == null) {
            log.error("Department with ID {} not found", departmentId);
            throw new DepartmentNotFoundException("Department not found with ID: " + departmentId);
        }

        user.setDepartment(department);
        log.info("User {} successfully assigned to department {}", userId, departmentId);
        return entityManager.merge(user);
    }

    public List<User> getAllUsers() {
        log.debug("Fetching all users");
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public List<User> getUsersByCity(String city) {
        log.debug("Fetching users by city: {}", city);
        if (city == null || city.trim().isEmpty()) {
            log.error("City parameter is invalid: {}", city);
            throw new InvalidInputException("City must not be null or empty");
        }

        String jpql = "SELECT u FROM User u WHERE u.address = :city";
        return entityManager.createQuery(jpql, User.class)
                .setParameter("city", city)
                .getResultList();
    }

    public Long countUsersByEmailDomain(String domain) {
        log.debug("Counting users by email domain: {}", domain);
        if (domain == null || domain.trim().isEmpty()) {
            log.error("Email domain is invalid: {}", domain);
            throw new InvalidInputException("Domain must not be null or empty");
        }

        String jpql = "SELECT COUNT(u) FROM User u WHERE u.email LIKE :domain";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("domain", "%" + domain)
                .getSingleResult();
    }

    public List<User> getUsersNative() {
        log.debug("Fetching all users using native SQL");
        String sql = "SELECT * FROM User";
        Query query = entityManager.createNativeQuery(sql, User.class);
        return query.getResultList();
    }

    public List<User> getUsersByCitySorted(String city) {
        log.debug("Fetching sorted users by city: {}", city);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.select(root)
                .where(cb.equal(root.get("address"), city))
                .orderBy(cb.desc(root.get("userName")));

        return entityManager.createQuery(query).getResultList();
    }

    public int updateEmailsByCity(String city, String newEmail) {
        log.info("Updating emails in city: {} to {}", city, newEmail);
        if (city == null || newEmail == null) {
            log.error("Invalid input for email update: city={}, email={}", city, newEmail);
            throw new InvalidInputException("City and email must not be null");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<User> update = cb.createCriteriaUpdate(User.class);
        Root<User> root = update.from(User.class);

        update.set("email", newEmail)
                .where(cb.equal(root.get("address"), city));

        return entityManager.createQuery(update).executeUpdate();
    }

    public int deleteUsersByCity(String city) {
        log.warn("Deleting users in city: {}", city);
        if (city == null || city.isBlank()) {
            log.error("City must not be blank for deleteUsersByCity");
            throw new InvalidInputException("City must not be null or blank");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<User> delete = cb.createCriteriaDelete(User.class);
        Root<User> root = delete.from(User.class);

        delete.where(cb.equal(root.get("address"), city));
        return entityManager.createQuery(delete).executeUpdate();
    }

    public List<User> getFilteredUsers(String city, String contact) {
        log.debug("Filtering users by city: {} and contact: {}", city, contact);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        Predicate finalPredicate = cb.conjunction(); //cb.conjunction() starts with a true predicate (like WHERE 1=1) to allow chaining.

        if (city != null && !city.isBlank()) {
            finalPredicate = cb.and(finalPredicate, cb.equal(root.get("address"), city));
        }
        if (contact != null && !contact.isBlank()) {
            finalPredicate = cb.and(finalPredicate, cb.equal(root.get("contact"), contact));
        }
        cq.select(root).where(finalPredicate);
        return entityManager.createQuery(cq).getResultList();
    }


    public List<User> getUsersSortedByNameAsc() {
        log.debug("Sorting users by name ascending");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.orderBy(cb.asc(root.get("userName")));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<User> getUsersSortedByEmailDesc() {
        log.debug("Sorting users by email descending");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.orderBy(cb.desc(root.get("email")));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<User> getUsersPaginated(int pageNo, int pageSize) {
        log.debug("Fetching users with pagination - page: {}, size: {}", pageNo, pageSize);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.select(root);

        TypedQuery<User> query = entityManager.createQuery(cq);
        query.setFirstResult((pageNo - 1) * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    public List<User> getUsersWithDepartments() {
        log.debug("Fetching users with department (INNER JOIN)");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        root.fetch("department", JoinType.INNER);
        cq.select(root).distinct(true);

        return entityManager.createQuery(cq).getResultList();
    }

    public List<User> getUsersByDepartmentName(String deptName) {
        log.debug("Fetching users by department name: {}", deptName);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        Join<User, Department> join = root.join("department");

        cq.where(cb.equal(join.get("deptName"), deptName));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<User> getUsersSortedByDepartmentName() {
        log.debug("Sorting users by department name descending");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        Join<User, Department> join = root.join("department");

        cq.select(root).orderBy(cb.desc(join.get("deptName")));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<UserSummaryDTO> getUserSummary() {
        log.debug("Fetching user summary using DTO projection");

        String jpql = "SELECT new com.wcs.spring_data_jpa_project.dto.UserSummaryDTO(u.userName, u.email, d.deptName) " +
                "FROM User u JOIN u.department d";

        return entityManager.createQuery(jpql, UserSummaryDTO.class)
                .getResultList();
    }


    public List<UserSummaryDTO> getUserSummaryByCity(String city) {
        log.debug("Fetching user summary by city: {}", city);
        String jpql = "SELECT new com.wcs.spring_data_jpa_project.dto.UserSummaryDTO(u.userName, u.email) " +
                "FROM User u WHERE u.address = :city";
        return entityManager.createQuery(jpql, UserSummaryDTO.class)
                .setParameter("city", city)
                .getResultList();
    }

    public List<UserDeptDTO> getUserDepartmentDetails() {
        log.debug("Fetching user-department DTO projection");
        String jpql = "SELECT new com.wcs.spring_data_jpa_project.dto.UserDeptDTO(u.userName, u.email, d.deptName) " +
                "FROM User u JOIN u.department d";
        return entityManager.createQuery(jpql, UserDeptDTO.class).getResultList();
    }

}
