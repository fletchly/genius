package org.fletchly.genius.context.data;

import com.zaxxer.hikari.HikariDataSource;

public interface DatabaseManager {
    HikariDataSource getDataSource();
    void close();
}
