package com.lithoapp.drainage.service;

import com.lithoapp.drainage.dto.*;

import java.util.List;
import java.util.UUID;

public interface DrainageService {

    DrainageResponse createDrainage(CreateDrainageRequest request);

    DrainageResponse updateDrainage(UUID id, UpdateDrainageRequest request);

    DrainageResponse removeDrainage(UUID id, RemoveDrainageRequest request);

    DrainageResponse getDrainageById(UUID id);

    List<DrainageResponse> getDrainages(DrainageFilterRequest filter);
}
