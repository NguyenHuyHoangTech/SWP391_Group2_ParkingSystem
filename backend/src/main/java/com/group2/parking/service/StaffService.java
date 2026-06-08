package com.group2.parking.service;

import com.group2.parking.entity.Account;
import com.group2.parking.repository.AccountRepository;
import com.group2.parking.repository.ParkingBuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

// Service contains staff creation and status update business rules for Staff Management.
@Service
@RequiredArgsConstructor
public class StaffService {

    // Newly created staff accounts are active by default.
    private static final String ACTIVE_STATUS = "ACTIVE";
    // Only staff-facing roles are accepted by the Add Staff flow.
    private static final Set<String> ALLOWED_ROLES = Set.of("STAFF", "MANAGER");
    // Status update flow is limited to activation and deactivation states.
    private static final Set<String> ALLOWED_STATUSES = Set.of("ACTIVE", "INACTIVE");
    // Email format used by backend validation before duplicate checks and persistence.
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    // Phone must contain only digits and match the expected local phone length.
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,11}$");

    private final AccountRepository accountRepository;
    private final ParkingBuildingRepository parkingBuildingRepository;

    /**
     * Returns accounts that are managed by the staff management screen.
     */
    public List<Account> getAllStaff() {
        return accountRepository.findByRoleIn(List.of("STAFF", "MANAGER"));
    }

    /**
     * Validates staff account input, checks duplicate username/email/phone, then persists a new account.
     */
    public Account createStaff(Account request) {
        validateCreateRequest(request);

        String username = request.getUsername().trim();
        String password = request.getPassword().trim();
        String email = normalizeOptional(request.getEmail());
        String phone = normalizeOptional(request.getPhone());
        String role = request.getRole().trim();

        // Duplicate checks are performed before save because email and phone may not have database unique constraints.
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

    /**
     * Updates a staff account status after validating that the requested status is supported.
     */
    public Account updateStaffStatus(Integer id, String status) {
        if (isBlank(status) || !ALLOWED_STATUSES.contains(status.trim())) {
            throw new IllegalArgumentException("Status must be ACTIVE or INACTIVE.");
        }

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff account not found."));

        account.setStatus(status.trim());

        return accountRepository.save(account);
    }

    // Centralized create validation keeps invalid direct API requests from reaching persistence.
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

        // Building assignment must point to an existing ParkingBuilding row.
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
