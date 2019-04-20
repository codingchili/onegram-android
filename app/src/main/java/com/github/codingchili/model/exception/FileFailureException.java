package com.github.codingchili.model.exception;

/**
 * @author Robin Duda
 * <p>
 * Throw when a file failed loading or saving.
 */

public class FileFailureException extends Exception {

    public FileFailureException() {}

    public FileFailureException(Exception e) {
        super(e);
    }
}
