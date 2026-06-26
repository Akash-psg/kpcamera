package com.iolite.kpcamera.attendance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iolite.kpcamera.attendance.model.RoomIpInbound;
import com.iolite.kpcamera.attendance.model.outbound.ApiResponse;
import com.iolite.kpcamera.attendance.model.outbound.RoomAndRoomDetailsOutbound;
import com.iolite.kpcamera.attendance.service.RoomIpService;

@RestController
@RequestMapping("/v1/roomAndIp")
public class RoomIpContoller {

	@Autowired
	RoomIpService roomIpService;
	
	@GetMapping("/getIpAndRoomDetails")
	public ResponseEntity<List<RoomAndRoomDetailsOutbound>> getIpAndRoomDetails() throws Exception{
		List<RoomAndRoomDetailsOutbound> roomIpOutbounds = roomIpService.getAllRoomDetails();
		return new ResponseEntity<>(roomIpOutbounds, HttpStatus.OK);
	}
	
	@PostMapping("/addRoomAndIp")
    public ResponseEntity<?> addRoomAndIp(@RequestBody RoomIpInbound roomIpRequest) {
        try {
            // Calling the service to handle the business logic
            boolean isSuccess = roomIpService.addRoomAndIp(roomIpRequest);
            if (isSuccess) {
                // Return a success response if the operation was successful
                return ResponseEntity.ok(new ApiResponse(true, "Room and IP added successfully!"));
            } else {
                // Return a failure response if the operation failed
                return ResponseEntity.status(400).body(new ApiResponse(false, "Failed to add room and IP"));
            }
        } catch (Exception e) {
            // Handle any exceptions that occur
            return ResponseEntity.status(500).body(new ApiResponse(false, "An error occurred while adding the room and IP"+e.getMessage()));
        }
    }
	
	// PUT method to update room and IP details
    @PutMapping("/updateRoomAndIp/{id}")
    public ResponseEntity<?> updateRoomAndIp(@PathVariable Integer id, @RequestBody RoomIpInbound request) {
        try {
        	boolean updatedRoom = true;
        	updatedRoom = roomIpService.updateRoomAndIp(id, request);
            if (updatedRoom) {
                return ResponseEntity.ok(new ApiResponse(true, "Room and IP updated successfully!"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Room not found"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Error updating room"));
        }
    }
	
	
	
}
