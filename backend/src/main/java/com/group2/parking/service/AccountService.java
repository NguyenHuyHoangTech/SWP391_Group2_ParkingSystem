package com.group2.parking.service;

import com.group2.parking.entity.Account;
import java.util.List;

public interface AccountService {
    List<Account> getAllStaffAndManagers();
    Account createStaff(Account account);
    Account updateStatus(Integer id, String status);
}
