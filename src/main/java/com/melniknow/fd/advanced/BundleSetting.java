package com.melniknow.fd.advanced;

import com.melniknow.fd.domain.Bookmaker;

public record BundleSetting(String name, boolean isValue, Bookmaker bk1, Bookmaker bk2) { }
