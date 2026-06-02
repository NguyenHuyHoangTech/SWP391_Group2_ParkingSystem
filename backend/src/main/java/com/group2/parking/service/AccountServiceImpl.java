package com.group2.parking.service;

import com.group2.parking.entity.Account;
import com.group2.parking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Account> getAllStaffAndManagers() {
        return accountRepository.findByRoleIn(Arrays.asList("STAFF", "MANAGER"));
    }

    @Override
    public Account createStaff(Account account) {
        account.setStatus("ACTIVE");
        return accountRepository.save(account);
    }

    @Override
    public Account updateStatus(Integer id, String status) {
        Optional<Account> accOpt = accountRepository.findById(id);
        if (accOpt.isPresent()) {
            Account acc = accOpt.get();
            acc.setStatus(status);
            return accountRepository.save(acc);
        }
        throw new RuntimeException("Account not found");
    }
}
