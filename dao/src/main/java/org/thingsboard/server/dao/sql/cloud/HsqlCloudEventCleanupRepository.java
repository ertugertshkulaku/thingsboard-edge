/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.sql.cloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.dao.sql.JpaAbstractDaoListeningExecutorService;
import org.thingsboard.server.dao.util.HsqlDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
@HsqlDao
@Repository
public class HsqlCloudEventCleanupRepository extends JpaAbstractDaoListeningExecutorService implements CloudEventCleanupRepository {

    @Override
    public void cleanupEvents(long eventsTtl) {
        long expirationTime = System.currentTimeMillis() - eventsTtl * 1000;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM cloud_event WHERE ts < ?")) {
            stmt.setLong(1, expirationTime);
            stmt.execute();
        } catch (SQLException e) {
            log.error("SQLException occurred during events TTL task execution ", e);
        }
    }
}
