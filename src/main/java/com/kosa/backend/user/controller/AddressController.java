package com.kosa.backend.user.controller;

import com.kosa.backend.user.dto.AddressDTO;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.AddressService;
import com.kosa.backend.user.service.UserService;
import com.kosa.backend.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    private final UserService userService;
    private final AddressService addressService;

    // 새로운 주소 등록
    @PostMapping("/new")
    public ResponseEntity<AddressDTO> createAddress(@AuthenticationPrincipal CustomUserDetails cud, @RequestBody AddressDTO addressDTO) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        addressDTO.setUserId(user.getId());
        System.out.println("-----addressDTO: " + addressDTO);
        AddressDTO createdAddress = addressService.createAddress(addressDTO);
        return ResponseEntity.ok(createdAddress);
    }

    // Address의 id로 주소 조회
    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable("id") int addrId) {
        AddressDTO addrDTO = addressService.getAddressById(addrId);
        if(addrDTO == null) { return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); }
        return ResponseEntity.ok(addrDTO);
    }

    // 인증된 사용자의 모든 주소 리스트 조회
    @PostMapping("/list")
    public ResponseEntity<List<AddressDTO>> getAddrList(@AuthenticationPrincipal CustomUserDetails cud) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        List<AddressDTO> addrDTOList = addressService.getAddressesByUserId(user.getId());
        return ResponseEntity.ok(addrDTOList);
    }

    // 특정 주소 수정
    @PutMapping("/{id}/upt")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable("id") int addrId, @RequestBody AddressDTO addressDTO) {
        AddressDTO updatedAddress = addressService.updateAddress(addrId, addressDTO);
        return ResponseEntity.ok(updatedAddress);
    }

    // 특정 주소 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("id") int addrId) {
        addressService.deleteAddress(addrId);
        return ResponseEntity.ok().build();
    }

    // 현재 인증된 사용자의 기본 주소 조회 (없을 시 null 반환)
    @PostMapping("/default")
    public ResponseEntity<AddressDTO> getDefaultAddr(@AuthenticationPrincipal CustomUserDetails cud) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        AddressDTO defaultAddrDTO = addressService.getDefaultAddr(user.getId());
        return ResponseEntity.ok(defaultAddrDTO);
    }
}
