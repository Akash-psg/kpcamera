package com.iolite.kpcamera.attendance.dao;

import java.util.Map;

public interface MegboxStudentDetailsDao {

	Map<String, String> getMegboxStudentDetails(
            String orgId, String ip);

}
