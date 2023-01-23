package com.cydeo.repository;

import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   User findByUsername(String username);

    User findUserById(Long id);

    @Query("Select u from User u where u.isDeleted=?1 order by u.company.title, u.role.description ")
    List<User> findAllUsersByCompanyAndRole(Boolean deleted);


    User findByUsernameAndIsDeleted(Long id, boolean b);

    //User findByEmail(String email);

    Boolean existsByUsername (String username);


    List<User> findAllByRole_Description(String admin);


    List<User> findAllByCompany_Title(Object currentUserCompanyTitle);


    List<User> findAllByCompany_TitleAndRole_Description(String companyTitle, String role);
}
