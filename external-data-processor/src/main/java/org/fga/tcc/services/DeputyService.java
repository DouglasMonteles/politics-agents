package org.fga.tcc.services;

import org.fga.tcc.entities.Deputy;
import org.fga.tcc.entities.DeputySpeech;

import java.util.List;

public interface DeputyService {

    List<Deputy> getDeputes();

    List<DeputySpeech> getDeputySpeech(Integer deputeId);

    void savePureData();

    void saveDeputiesSpeechesByType();
}
