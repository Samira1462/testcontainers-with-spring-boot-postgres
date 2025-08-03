package com.retailordersystem.api.constants;

/**
 * Defines Docker image names used across the application.
 * This class cannot be instantiated or extended.
 */
public final class DockerImageConstants {

    public static final String POSTGRES_IMAGE = "postgres:16-alpine";

    private DockerImageConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}
