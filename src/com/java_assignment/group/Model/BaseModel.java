package com.java_assignment.group.Model;

/**
 * This interface should be implemented by all models.
 * Each model must provide methods to get and set a unique identifier.
 */
public interface BaseModel {
    String getId();
    void setId(String id);
}
