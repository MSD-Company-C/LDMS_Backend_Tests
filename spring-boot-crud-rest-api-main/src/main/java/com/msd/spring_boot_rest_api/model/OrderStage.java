package com.msd.spring_boot_rest_api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;

@Embeddable
public class OrderStage {

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "is_completed")
    private boolean completed;

    public OrderStage() {
        this.timestamp = null; // null = not happened yet
        this.completed = false;
    }

    public OrderStage(LocalDateTime timestamp, boolean completed) {
        this.timestamp = timestamp;
        this.completed = completed;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;

        // 💡 Enforce consistency: if timestamp is set manually, also mark as completed
        if (timestamp != null) {
            this.completed = true;
        }
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;

        // 💡 If someone marks it as completed but timestamp is still null, set
        // timestamp now
        if (completed && this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    @Transient
    public String getDisplayTimestamp() {
        return (timestamp == null) ? "Yet to happen" : timestamp.toString();
    }
}
