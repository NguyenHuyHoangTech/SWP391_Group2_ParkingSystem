package com.group2.parking.service;

import com.group2.parking.dto.StaffCreateRequest;
import com.group2.parking.dto.StaffResponse;
import com.group2.parking.dto.StaffStatusUpdateRequest;
import com.group2.parking.entity.Account;
import com.group2.parking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StaffService {

    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final Set<String> ALLOWED_ROLES = Set.of("STAFF", "MANAGER");
    private static final Set<String> ALLOWED_STATUSES = Set.of("ACTIVE", "INACTIVE", "BANNED");

    private final AccountRepository accountRepository;

    public List<StaffResponse> getAllStaff() {
        List<Account> accounts = accountRepository.findByRoleIn(List.of("STAFF", "MANAGER"));

        return accounts.stream()
                .map(this::toStaffResponse)
                .toList();
    }

    public StaffResponse createStaff(StaffCreateRequest request) {
        validateCreateRequest(request);

        String username = request.getUsername().trim();
        String password = request.getPassword().trim();
        String role = request.getRole().trim();

        if (accountRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        Account account = Account.builder()
                .username(username)
                .password(password)
                .email(normalizeOptional(request.getEmail()))
                .phone(normalizeOptional(request.getPhone()))
                .role(role)
                .buildingId(request.getBuildingId())
                .status(ACTIVE_STATUS)
                .build();

        return toStaffResponse(accountRepository.save(account));
    }

    public StaffResponse updateStaffStatus(Integer id, StaffStatusUpdateRequest request) {
        validateStatusUpdateRequest(request);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff account not found."));

        account.setStatus(request.getStatus().trim());

        return toStaffResponse(accountRepository.save(account));
    }

    private void validateCreateRequest(StaffCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Staff data is required.");
        }

        if (isBlank(request.getUsername())) {
            throw new IllegalArgumentException("Username is required.");
        }

        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException("Password is required.");
        }

        if (isBlank(request.getRole()) || !ALLOWED_ROLES.contains(request.getRole().trim())) {
            throw new IllegalArgumentException("Role must be STAFF or MANAGER.");
        }
    }

    private void validateStatusUpdateRequest(StaffStatusUpdateRequest request) {
        if (request == null || isBlank(request.getStatus())) {
            throw new IllegalArgumentException("Status is required.");
        }

        if (!ALLOWED_STATUSES.contains(request.getStatus().trim())) {
            throw new IllegalArgumentException("Status must be ACTIVE, INACTIVE, or BANNED.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeOptional(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    private StaffResponse toStaffResponse(Account account) {
        return StaffResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .phone(account.getPhone())
                .role(account.getRole())
                .buildingId(account.getBuildingId())
                .status(account.getStatus())
                .build();
    }
}
