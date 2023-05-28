package com.melniknow.fd.advanced;

import com.melniknow.fd.domain.Bookmaker;

public record BundleSetting(String name, boolean isValue, boolean isVerifiedValue, Bookmaker bk1, Bookmaker bk2) { }
