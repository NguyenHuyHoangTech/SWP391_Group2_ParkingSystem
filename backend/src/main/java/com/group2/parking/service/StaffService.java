package com.group2.parking.service;

import com.group2.parking.entity.Account;
import com.group2.parking.repository.AccountRepository;
import com.group2.parking.repository.ParkingBuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class StaffService {

    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final Set<String> ALLOWED_ROLES = Set.of("STAFF", "MANAGER");
    private static final Set<String> ALLOWED_STATUSES = Set.of("ACTIVE", "INACTIVE");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,11}$");

    private final AccountRepository accountRepository;
    private final ParkingBuildingRepository parkingBuildingRepository;

    public List<Account> getAllStaff() {
        return accountRepository.findByRoleIn(List.of("STAFF", "MANAGER"));
    }

    public Account createStaff(Account request) {
        validateCreateRequest(request);

        String username = request.getUsername().trim();
        String password = request.getPassword().trim();
        String email = normalizeOptional(request.getEmail());
        String phone = normalizeOptional(request.getPhone());
        String role = request.getRole().trim();

        if (accountRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (email != null && accountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (phone != null && accountRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        Account account = Account.builder()
                .username(username)
                .password(password)
                .email(email)
                .phone(phone)
                .role(role)
                .buildingId(request.getBuildingId())
                .status(ACTIVE_STATUS)
                .build();

        return accountRepository.save(account);
    }

    public Account updateStaffStatus(Integer id, String status) {
        if (isBlank(status) || !ALLOWED_STATUSES.contains(status.trim())) {
            throw new IllegalArgumentException("Status must be ACTIVE or INACTIVE.");
        }

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff account not found."));

        account.setStatus(status.trim());

        return accountRepository.save(account);
    }

    private void validateCreateRequest(Account request) {
        if (request == null) {
            throw new IllegalArgumentException("Staff data is required.");
        }

        if (isBlank(request.getUsername())) {
            throw new IllegalArgumentException("Username is required.");
        }

        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException("Password is required.");
        }

        if (isBlank(request.getEmail())) {
            throw new IllegalArgumentException("Email is required.");
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        if (isBlank(request.getPhone())) {
            throw new IllegalArgumentException("Phone is required.");
        }

        if (!PHONE_PATTERN.matcher(request.getPhone().trim()).matches()) {
            throw new IllegalArgumentException("Invalid phone number format.");
        }

        if (isBlank(request.getRole()) || !ALLOWED_ROLES.contains(request.getRole().trim())) {
            throw new IllegalArgumentException("Role must be STAFF or MANAGER.");
        }

        if (request.getBuildingId() == null) {
            throw new IllegalArgumentException("Building ID is required.");
        }

        if (!parkingBuildingRepository.existsById(request.getBuildingId())) {
            throw new IllegalArgumentException("Building does not exist.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeOptional(String value) {
        return isBlank(value) ? null : value.trim();
    }
}
