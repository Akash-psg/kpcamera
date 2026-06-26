package com.iolite.kpcamera.attendance.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import com.iolite.kpcamera.attendance.dao.RoomDao;
import com.iolite.kpcamera.attendance.mapper.GetRoomDetailsMapper;
import com.iolite.kpcamera.attendance.mapper.RoomAndRoomDetailsMapper;
import com.iolite.kpcamera.attendance.mapper.RoomAndRoomDetailsMapperDto;
import com.iolite.kpcamera.attendance.mapper.RoomDetailsMapper;
import com.iolite.kpcamera.attendance.mapper.RoomMapper;
import com.iolite.kpcamera.attendance.model.RoomIpInbound;
import com.iolite.kpcamera.attendance.model.RoomReqDto;
import com.iolite.kpcamera.attendance.model.outbound.RoomIpOutbound;
import com.iolite.kpcamera.attendance.model.responses.RoomResDataDto;
import com.iolite.kpcamera.attendance.model.responses.RoomResDto;
import com.iolite.kpcamera.attendance.service.MegBoxAttendanceService;

@Repository
public class RoomDaoImpl implements RoomDao{
	
	
	@Autowired
	@Qualifier("cristMysoreJDBC")
	private JdbcTemplate cristMysoreJDBC;
	
	@Autowired
	@Qualifier("middleWareJDBC")
	private JdbcTemplate middleWareJDBC;
	
	static final Logger lg = LoggerFactory.getLogger(RoomDaoImpl.class);

	private static final String QUERY = "select * from room_master where is_active = 1";
	
	private static final String INSERT_QUERY_FOR_ROOM = "INSERT INTO group_data(box_ip, discription, is_active) VALUES (?, ?, ?)";
    private static final String INSERT_QUERY_FOR_ROOM_DETAILS = "INSERT INTO group_details(group_name, update_by, update_date, modified_by, modified_date, is_active, floor, camera_ip, room_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    
    private static final String GET_ALL_ROOM = "select r.id as id, rd.group_name as groupName,rd.camera_ip as cameraIp,"
    		+ "rd.floor as floor,r.box_ip as boxIp,r.discription as description from group_details as rd join group_data as r on rd.room_id = r.id and rd.is_active = true and r.is_active = 1";

	
	private static final String INSERT_QUERY = "INSERT INTO room(\r\n" + 
			"   org_room_id\r\n" + 
			"  ,org_id\r\n" + 
			"  ,org_loc_id\r\n" + 
			"  ,org_facility_id\r\n" + 
			"  ,room_id\r\n" + 
			"  ,update_by\r\n" + 
			"  ,update_date\r\n" + 
			"  ,modified_by\r\n" + 
			"  ,modified_date\r\n" + 
			"  ,is_active\r\n" + 
			") VALUES (?,?,?,?,?,?,?,?,?,?)";
	
	private static final String GET_ROOM = "select org_room_id,room_id from room";
	
	private static final String GET_GROUP_DATA = "SELECT id AS id, box_ip,discription FROM group_data WHERE is_active = 1 AND box_ip =";

	
	private static final String GET_ROOM_BY_ID = "select r.id as id, rd.group_name as groupName,rd.camera_ip as cameraIp,"
    		+ "rd.floor as floor,r.box_ip as boxIp,r.discription as description from group_details as rd"
    		+ " join group_data as r on rd.room_id = r.id and rd.is_active = true and r.is_active = 1 and r.id =";

	private static final String DELETE_GROUP_DATA = "delete from group_data where id = ?";
	
	private static final String DELETE_GROUP_DETAILS = "delete from group_details where room_id = ?";
	
	@Override
	public List<RoomReqDto> getRoomMaster(int id) throws Exception {
		return cristMysoreJDBC.query(QUERY, new RoomMapper());
	}
	@Override
	public void saveRoom(RoomResDto roomResDto, int id, String orgLocId, String org_facility_id) {
		middleWareJDBC.update(INSERT_QUERY, roomResDto.getData().getOrg_room_id(),
				id,orgLocId,org_facility_id,roomResDto.getData().getRoom_id(),"",new Date(),"",new Date(),1);
	}
	@Override
	public Map<String, String> getAwsRoomIds(int id) throws Exception {
		DriverManagerDataSource datasource = new DriverManagerDataSource();
		datasource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		datasource.setUrl("jdbc:mysql://172.16.16.5:3306/kp_camera?zeroDateTimeBehavior=convertToNull");
		datasource.setUsername("microhard");
		datasource.setPassword("microhard123");
		JdbcTemplate middleWareJDBCNew = new JdbcTemplate(datasource);
		return middleWareJDBCNew.query(GET_ROOM,new GetRoomDetailsMapper()).stream().collect(
                Collectors.toMap(RoomResDataDto::getOrg_room_id, RoomResDataDto::getRoom_id));
	}
	@Override
    public boolean saveRoom(RoomIpInbound roomIpInbound) {
        try {
            // Insert into room and fetch roomId
            int rowsAffected = middleWareJDBC.update(INSERT_QUERY_FOR_ROOM, roomIpInbound.getIpAddress(), roomIpInbound.getDescription(), true);

            if (rowsAffected > 0) {
                // Fetch room details to get the roomId
                RoomIpOutbound roomIpOutbound = getRoom(roomIpInbound);

                // Use batch update to insert all camera IP details for the room in one go
                List<Object[]> cameraDetails = roomIpInbound.getCameraIps().stream()
                        .map(camera -> new Object[]{
                                camera.getGroupName(),
                                "", // Assuming empty string for update_by
                                new Date(), // Assuming current date for update_date
                                "", // Assuming empty string for modified_by
                                new Date(), // Assuming current date for modified_date
                                1, // Active status (1)
                                camera.getFloor(),
                                camera.getCameraIp(),
                                roomIpOutbound.getId() // roomId
                        })
                        .collect(Collectors.toList());

                // Batch insert all camera IP details
                middleWareJDBC.batchUpdate(INSERT_QUERY_FOR_ROOM_DETAILS, cameraDetails);

                return true;
            }
        } catch (Exception e) {
            // Log the exception and rollback
            System.err.println("Error saving room: " + e.getMessage());
            throw e; // Transaction management will handle the rollback
        }

        return false;
    }
	
	@Override
    public List<RoomAndRoomDetailsMapperDto> getRoomDetails() throws Exception {
        return middleWareJDBC.query(GET_ALL_ROOM, new RoomAndRoomDetailsMapper());
    }
	
	@Override
	public boolean deleteRoomAndIp(Integer id) {
	    try {
	        int detailsDeleted = middleWareJDBC.update(DELETE_GROUP_DETAILS, id);
	        if (detailsDeleted > 0) {
	            int dataDeleted = middleWareJDBC.update(DELETE_GROUP_DATA, id);
	            return dataDeleted > 0;
	        }
	    } catch (Exception e) {
	        System.err.println("Error deleting room and IP: " + e.getMessage());
	    }
	    return false;
	}

	// Get room by IP address
	@Override
    public RoomIpOutbound getRoom(RoomIpInbound roomIpInbound) {
    	String query = GET_GROUP_DATA+"'"+roomIpInbound.getIpAddress()+"'";
		return middleWareJDBC.query(query, new RoomDetailsMapper()).size() > 0 ? middleWareJDBC.query(query, new RoomDetailsMapper()).get(0) : null;
    }
	@Override
	public List<RoomAndRoomDetailsMapperDto> getRoomDetailsById(Integer id) {
		String query = GET_ROOM_BY_ID+id;
 		return middleWareJDBC.query(query, new RoomAndRoomDetailsMapper());
	}
	
	private static final String GET_MEGBOX_BY_CAMERA =
		    "SELECT m.megbox_ip " +
		    "FROM camera_device c " +
		    "JOIN megbox_device m ON c.megbox_id = m.id " +
		    "WHERE c.camera_ip = ? " +
		    "AND c.is_active = 1 " +
		    "AND m.is_active = 1 " +
		    "LIMIT 1";
       
	    
	@SuppressWarnings("deprecation")
	@Override
	public String getMegBoxIpByCameraIp(String cameraIp) {
	    try {
	        // ADD THESE TWO LINES:
	        String dbUrl = middleWareJDBC.getDataSource().getConnection().getMetaData().getURL();
	        lg.info("getMegBoxIpByCameraIp - DB URL: {}, cameraIp: {}", dbUrl, cameraIp);

	        List<String> result = middleWareJDBC.query(
	                GET_MEGBOX_BY_CAMERA,
	                new Object[]{cameraIp},
	                (rs, rowNum) -> rs.getString("megbox_ip"));

	        lg.info("getMegBoxIpByCameraIp - result size: {}, result: {}", result.size(), result);

	        return result.isEmpty() ? null : result.get(0);

	    } catch (Exception e) {
	        lg.error("Error getting MegBox IP for camera: {}", cameraIp, e);
	        return null;
	    }
	}
}
