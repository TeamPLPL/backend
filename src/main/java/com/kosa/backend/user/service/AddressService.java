package com.kosa.backend.user.service;

import com.kosa.backend.user.dto.AddressDTO;
import com.kosa.backend.user.entity.Address;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.AddressRepository;
import com.kosa.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddressDTO createAddress(AddressDTO addressDTO) {
        User user = userRepository.findById(addressDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found for ID: " + addressDTO.getUserId()));

        // isDefault 이미 있는지 여부 체크
        if(addressDTO.isDefault()) {
            Optional<Address> defaultAddress = addressRepository.findByUserIdAndIsDefaultTrue(user.getId());
            if(defaultAddress.isPresent()) {
                // 기존 기본 배송지 false로 업데이트
                Address temp = defaultAddress.get();
                temp.setDefault(false);
                addressRepository.save(temp);
            }
        }

        Address address = new Address();
        address.setZonecode(addressDTO.getZonecode());
        address.setAddr(addressDTO.getAddr());
        address.setAddrEng(addressDTO.getAddrEng());
        address.setDetailAddr(addressDTO.getDetailAddr());
        address.setExtraAddr(addressDTO.getExtraAddr());
        address.setDefault(addressDTO.isDefault());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        return mapToDTO(savedAddress);
    }

    public List<AddressDTO> getAddressesByUserId(int userId) {
        List<Address> addresses = addressRepository.findAllByUserId(userId);
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public AddressDTO updateAddress(int id, AddressDTO addressDTO) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found for ID: " + id));

        address.setZonecode(addressDTO.getZonecode());
        address.setAddr(addressDTO.getAddr());
        address.setAddrEng(addressDTO.getAddrEng());
        address.setDetailAddr(addressDTO.getDetailAddr());
        address.setExtraAddr(addressDTO.getExtraAddr());
        address.setDefault(addressDTO.isDefault());

        Address updatedAddress = addressRepository.save(address);

        return mapToDTO(updatedAddress);
    }

    public void deleteAddress(int id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found for ID: " + id));

        addressRepository.delete(address);
    }

    private AddressDTO mapToDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setZonecode(address.getZonecode());
        dto.setAddr(address.getAddr());
        dto.setAddrEng(address.getAddrEng());
        dto.setDetailAddr(address.getDetailAddr());
        dto.setExtraAddr(address.getExtraAddr());
        dto.setDefault(address.isDefault());
        dto.setUserId(address.getUser().getId());
//        return AddressDTO.builder()
//                .id(address.getId())
//                .zonecode(address.getZonecode())
//                .addr(address.getAddr())
//                .addrEng(address.getAddrEng())
//                .detailAddr(address.getDetailAddr())
//                .extraAddr(address.getExtraAddr())
//                .isDefault(address.isDefault())
//                .userId(address.getUser().getId())
//                .build();

        return dto;
    }

    public AddressDTO getDefaultAddr(int userId) {
        Address addr = addressRepository.findByUserIdAndIsDefaultTrue(userId).orElse(null);
        if(addr == null) { return null; }
        return mapToDTO(addr);
    }

    public AddressDTO getAddressById(int addrId) {
        Address addr = addressRepository.findById(addrId).orElse(null);
        if(addr == null) { return null; }
        return mapToDTO(addr);
    }
}
