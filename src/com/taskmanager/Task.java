package com.taskmanager;

public class Task {
    private int id;
    private String title;
    private String description;
    private String priority;
    private String deadline;
    private String status;
    private boolean completed;

    // Full constructor (matches places that construct Task with all fields)
    public Task(int id, String title, String description, String priority,
                String deadline, String status, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.status = status;
        this.completed = completed;
    }

    // Convenience constructor (if some code constructs without id)
    public Task(String title, String description, String priority,
                String deadline, String status, boolean completed) {
        this(0, title, description, priority, deadline, status, completed);
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public String getDeadline() { return deadline; }
    public String getStatus() { return status; }
    public boolean isCompleted() { return completed; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setStatus(String status) { this.status = status; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
