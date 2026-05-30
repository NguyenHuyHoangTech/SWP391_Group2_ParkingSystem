package com.group2.parking.dto;

public record CreateFeedbackRequest(
        Integer accountId,
        String title,
        String description
) {}