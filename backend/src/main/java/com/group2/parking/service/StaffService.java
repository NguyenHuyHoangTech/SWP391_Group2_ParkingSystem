package com.group2.parking.service;

import com.group2.parking.dto.StaffCreateRequest;
import com.group2.parking.dto.StaffResponse;
import com.group2.parking.dto.StaffStatusUpdateRequest;
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

    private void validateStatusUpdateRequest(StaffStatusUpdateRequest request) {
        if (request == null || isBlank(request.getStatus())) {
            throw new IllegalArgumentException("Status is required.");
        }

        if (!ALLOWED_STATUSES.contains(request.getStatus().trim())) {
            throw new IllegalArgumentException("Status must be ACTIVE or INACTIVE.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeOptional(String value) {
        return isBlank(value) ? null : value.trim();
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
