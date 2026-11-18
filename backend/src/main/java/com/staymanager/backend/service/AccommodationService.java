package com.staymanager.backend.service;

import com.staymanager.backend.dto.AccommodationCreateRequest;
import com.staymanager.backend.dto.AccommodationResponse;
import com.staymanager.backend.entity.Accommodation;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;
    public List<Accommodation> getAllAccommodations(){
        return accommodationRepository.findAll();
    }
    public Accommodation getAccommodationById(Long id){
        return accommodationRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Accommodation not found"));
    }
    public List<Accommodation> getAccommodationsByCity(String city){
        return accommodationRepository.findByCityIgnoreCase(city);
    }
    public AccommodationResponse createAccommodation(AccommodationCreateRequest request, User owner){
        Accommodation accommodation = new Accommodation();
        accommodation.setAddress(request.getAddress());
        accommodation.setCity(request.getCity());
        accommodation.setName(request.getName());
        accommodation.setDescription(request.getDescription());
        accommodation.setPricePerNight(request.getPricePerNight());
        accommodation.setCapacity(request.getCapacity());
        accommodation.setOwner(owner);
        return toResponse(accommodationRepository.save(accommodation));
    }
    public AccommodationResponse updateAccommodation(Long id, AccommodationCreateRequest request){
        Accommodation accommodation = getAccommodationById(id);
        accommodation.setCity(request.getCity());
        accommodation.setName(request.getName());
        accommodation.setAddress(request.getAddress());
        accommodation.setDescription(request.getDescription());
        accommodation.setPricePerNight(request.getPricePerNight());
        accommodation.setCapacity(request.getCapacity());
        return toResponse(accommodationRepository.save(accommodation));
    }
    public void deleteAccommodation(Long id){
        Accommodation accommodation = getAccommodationById(id);
        accommodationRepository.delete(accommodation);
    }

    private AccommodationResponse toResponse(Accommodation accommodation){
        AccommodationResponse response = new AccommodationResponse();
        response.setId(accommodation.getId());
        response.setName(accommodation.getName());
        response.setDescription(accommodation.getDescription());
        response.setCity(accommodation.getCity());
        response.setCapacity(accommodation.getCapacity());
        response.setPricePerNight(accommodation.getPricePerNight());
        response.setAddress(accommodation.getAddress());
        response.setOwnerId(accommodation.getOwner().getId());
        return response;
    }
    public AccommodationResponse getAccommodationResponseById(Long id){
        return toResponse(getAccommodationById(id));
    }
    public List<AccommodationResponse> getAllAccommodationResponses(){
        return getAllAccommodations().stream().map(accommodation -> toResponse(accommodation)).toList();
    }
    public List<AccommodationResponse> getAccommodationResponsesByCity(String city) {
        return getAccommodationsByCity(city).stream().map(accommodation -> toResponse(accommodation)).toList();
    }

}
