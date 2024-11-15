package org.fga.tcc.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.fga.tcc.entities.Deputy;
import org.fga.tcc.entities.DeputySpeech;
import org.fga.tcc.entities.OpenDataBaseResponseList;
import org.fga.tcc.enums.OpenDataEndpoints;
import org.fga.tcc.json.FetchJson;
import org.fga.tcc.json.RouterManager;
import org.fga.tcc.services.DeputyService;
import org.fga.tcc.utils.FileUtils;
import org.fga.tcc.utils.ResourceUtils;

import java.io.File;
import java.util.List;

public class DeputyServiceImpl implements DeputyService {

    @Override
    public List<Deputy> getDeputes() {
        RouterManager routerManager = new RouterManager();
        FetchJson<Deputy> fetchJson = new FetchJson<>();
        OpenDataBaseResponseList<Deputy> deputeOpenDataBaseResponse = fetchJson.get(
                routerManager
                        .setUrl(OpenDataEndpoints.API_DEPUTES_URL.getPath())
                        .setPage(1)
                        .setOrderBy("nome")
                        .getUrl(),
                new TypeReference<>() {}
        );

        return deputeOpenDataBaseResponse.getData();
    }

    @Override
    public List<DeputySpeech> getDeputySpeech(Integer deputeId) {
        RouterManager routerManager = new RouterManager();
        FetchJson<DeputySpeech> fetchJson = new FetchJson<>();
        OpenDataBaseResponseList<DeputySpeech> deputySpeechOpenDataBaseResponse = fetchJson.get(
                routerManager
                        .setUrl(OpenDataEndpoints.API_DEPUTES_URL.getPath())
                        .setRequestParamId(deputeId)
                        .setRequestUri("discursos")
                        .setInterval("2008-01-01", "2023-12-31")
                        .setItems(1_000_000)
                        .getUrl(),
                new TypeReference<>() {}
        );

        return deputySpeechOpenDataBaseResponse.getData();
    }

    @Override
    public void savePureData() {
        for (Deputy deputy : getDeputes()) {
            int speechCont = 0;
            List<DeputySpeech> deputySpeeches = getDeputySpeech(deputy.getId());

            for (DeputySpeech deputySpeech : deputySpeeches) {
                String path = ResourceUtils.RESOURCE_TRAINING_PURE_DATA_PATH + "/" + "deputies/speeches/" + deputy.getId() + "/" + speechCont + ".txt";
                File file = FileUtils.createFile(path);

                FileUtils.saveTxtFile(file.getAbsolutePath(), deputySpeech.getSummary());
                speechCont++;
            }
        }
    }

    @Override
    public void saveDeputiesSpeechesByType() {
        for (Deputy deputy : getDeputes()) {
            List<DeputySpeech> deputySpeeches = getDeputySpeech(deputy.getId());

            for (DeputySpeech deputySpeech : deputySpeeches) {
                String speechType = deputySpeech.getSpeechType();

                if (speechType != null && !speechType.isEmpty()) {
                    String path = getNormalizedSpeechType(deputySpeech);
                    File file = FileUtils.createFile(path);

                    FileUtils.saveTxtFile(file.getAbsolutePath(), deputySpeech.getSummary());
                }
            }
        }
    }

    private String getNormalizedSpeechType(DeputySpeech deputySpeech) {
        /*
        * If we use a cont variable, it's possible that the two or more
        * speeches (different deputies) has the same cont. Then, in this case,
        * the timestamp will be used instead.
        * */
        long timestamp = System.currentTimeMillis();

        String normalizedSpeechType = deputySpeech.getSpeechType()
                .replaceAll(" ", "_")
                .replaceAll("Í", "I")
                .replaceAll("Ã", "A")
                .replaceAll("Ç", "C")
                .replaceAll("Õ", "O")
                .replaceAll("Ê", "E")
                .trim();

        return ResourceUtils.RESOURCE_TRAINING_PURE_DATA_PATH + "/" + "deputies/speechesTypes/" + normalizedSpeechType + "/" + timestamp + ".txt";
    }

}
