package com.melniknow.fd.advanced;

import com.melniknow.fd.domain.Sport;

public record Exception(String name, Sport sport, ExceptionType type) {
}
