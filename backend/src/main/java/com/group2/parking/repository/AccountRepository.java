package com.group2.parking.repository;

import com.group2.parking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository contains account lookup methods used by staff management and validation.
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    // Derived query returns staff and manager accounts for staff list display.
    List<Account> findByRoleIn(List<String> roles);

    // Derived query checks username duplication before creating a staff account.
    boolean existsByUsername(String username);

    // Derived query checks email duplication before creating a staff account.
    boolean existsByEmail(String email);

    // Derived query checks phone duplication before creating a staff account.
    boolean existsByPhone(String phone);
}
