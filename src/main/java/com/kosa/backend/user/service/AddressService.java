package com.kosa.backend.user.service;

import com.kosa.backend.user.dto.AddressDTO;
import com.kosa.backend.user.entity.Address;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.AddressRepository;
import com.kosa.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressDTO createAddress(AddressDTO addressDTO) {
        User user = userRepository.findById(addressDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found for ID: " + addressDTO.getUserId()));

        Address address = new Address();
        address.setZonecode(addressDTO.getZonecode());
        address.setAddr(addressDTO.getAddr());
        address.setAddrEng(addressDTO.getAddrEng());
        address.setDetailAddr(addressDTO.getDetailAddr());
        address.setDefault(addressDTO.isDefault());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        return mapToDTO(savedAddress);
    }

    public List<AddressDTO> getAddressesByUserId(int userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public AddressDTO updateAddress(int id, AddressDTO addressDTO) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found for ID: " + id));

        address.setZonecode(addressDTO.getZonecode());
        address.setAddr(addressDTO.getAddr());
        address.setAddrEng(addressDTO.getAddrEng());
        address.setDetailAddr(addressDTO.getDetailAddr());
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
        dto.setDefault(address.isDefault());
        dto.setUserId(address.getUser().getId());
        return dto;
    }
}
