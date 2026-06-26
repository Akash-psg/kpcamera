package com.iolite.kpcamera.attendance.dao;

import java.util.List;
import java.util.Map;

import com.iolite.kpcamera.attendance.mapper.RoomAndRoomDetailsMapperDto;
import com.iolite.kpcamera.attendance.model.RoomIpInbound;
import com.iolite.kpcamera.attendance.model.RoomReqDto;
import com.iolite.kpcamera.attendance.model.outbound.RoomIpOutbound;
import com.iolite.kpcamera.attendance.model.responses.RoomResDto;

public interface RoomDao {
	
	List<RoomReqDto> getRoomMaster(int id) throws Exception;

	void saveRoom(RoomResDto roomResDto, int id, String orgLocId, String org_facility_id);
	
	Map<String, String> getAwsRoomIds(int id) throws Exception;

	boolean saveRoom(RoomIpInbound roomIpInbound);

	List<RoomAndRoomDetailsMapperDto> getRoomDetails() throws Exception;

	boolean deleteRoomAndIp(Integer id);

	List<RoomAndRoomDetailsMapperDto> getRoomDetailsById(Integer id);

	RoomIpOutbound getRoom(RoomIpInbound roomIpInbound);
	
	String getMegBoxIpByCameraIp(String cameraIp);


}
