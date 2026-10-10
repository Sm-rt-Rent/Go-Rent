package com.Vhytor.GoRent.services;

import com.Vhytor.GoRent.dtos.request.CreateHomeRequest;
import com.Vhytor.GoRent.model.Home;
import com.Vhytor.GoRent.model.User;
import com.Vhytor.GoRent.model.ViewingRecord;

import java.util.List;

public interface LandlordService {
    List<Home> getMyProperties(User landlord);
    List<ViewingRecord> getMyViewingRecords(long homeId, User landlord);
    Home createProperty(CreateHomeRequest request, User landlord);
    void deleteProperty(Long homeId, User landlord);
}
