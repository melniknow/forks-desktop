package com.melniknow.fd.advanced;

import com.melniknow.fd.domain.Sport;

/**
 * @param isException Может быть или правило, или исключение
 */
public record Rule(String name, Sport sport, RuleType type, boolean isException) {
}
