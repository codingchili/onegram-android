package com.codingchili.onegram.model.api.exception;

/**
 * @author Robin Duda
 *
 * Throw when the server already has the
 * content, when registering an existing
 * email for example.
 */

public class QueryConflictException extends QueryException {
}
