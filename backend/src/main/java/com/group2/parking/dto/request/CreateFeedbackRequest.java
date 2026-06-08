package com.group2.parking.dto.request;

public record CreateFeedbackRequest(
        Integer accountId,
        String title,
        String description
) {}