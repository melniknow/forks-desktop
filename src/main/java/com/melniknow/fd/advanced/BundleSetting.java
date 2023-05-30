package com.melniknow.fd.advanced;

import com.melniknow.fd.domain.Bookmaker;

// Структура данных для хранения настроек связок
public record BundleSetting(String name, boolean isValue, boolean isVerifiedValue, Bookmaker bk1, Bookmaker bk2) { }
