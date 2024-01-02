/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.Properties;

public class JdbcTests {


    private static final Logger LOG = LoggerFactory.getLogger(JdbcTests.class);


    @Test
    public void connection() throws Exception {
        try (Connection conn = createPostgreConnection()) {
            String sql = "INSERT INTO\n" +
                    "               china_region(`name`, region_type)\n" +
                    "               VALUES\n" +
                    "                   ('五指碓', 0),\n" +
                    "                   ('华北', 0),\n" +
                    "                   ('西北', 0),\n" +
                    "                   ('外蒙', 0),\n" +
                    "                   ('华南', 0),\n" +
                    "                   ('东北', 0)\n" +
                    "           ON DUPLICATE KEY UPDATE\n" +
                    "               version = version + 1";
            try (Statement stmt = conn.createStatement()) {
                int rows;
                rows = stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                LOG.debug("rows:{}", rows);
                try (ResultSet resultSet = stmt.getGeneratedKeys()) {
                    while (resultSet.next()) {
                        System.out.println(resultSet.getString(1));
                    }
                }
            }

        }

    }


    @Test
    public void indexMeta() throws Exception {
        try (Connection conn = createPostgreConnection()) {
            final DatabaseMetaData metaData;
            metaData = conn.getMetaData();

            try (ResultSet resultSet = metaData.getIndexInfo("army_bank", "public", "china_region", true, true)) {
                while (resultSet.next()) {
                    LOG.debug(resultSet.getString("INDEX_NAME"));
                }

            }

        }
    }


    private Connection createConnection() {
        String url = "jdbc:mysql://localhost:3306/army_bank?sslMode=DISABLED";
        Properties properties = new Properties();
        properties.setProperty("user", "army_w");
        properties.setProperty("password", "army123");
        try {
            return DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection createPostgreConnection() {
        String url = "jdbc:postgresql://localhost:5432/army_bank";
        Properties properties = new Properties();
        properties.setProperty("user", "army_w");
        properties.setProperty("password", "army123");
        try {
            return DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
