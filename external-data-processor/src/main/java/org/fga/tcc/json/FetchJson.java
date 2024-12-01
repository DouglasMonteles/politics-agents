package org.fga.tcc.json;

import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fga.tcc.entities.OpenDataBaseResponseList;
import org.fga.tcc.entities.OpenDataBaseSingleResponse;
import org.fga.tcc.utils.FileUtils;
import org.fga.tcc.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FetchJson<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchJson.class);

    private int retry = 0;

    public OpenDataBaseResponseList<T> get(String url, TypeReference<OpenDataBaseResponseList<T>> typeReference) {
        try {
            UriParams uriParams = extractUri(url);
            String filePath = getFilePath(uriParams);

            if (!new File(filePath).exists()) {
                LOGGER.info("Sending GET request: " + url);
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    saveResponseInLocalCache(url, response.body(), true);

                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(response.body(), typeReference);
                } else {
                    LOGGER.error("Error sending request. Status code: " + response.statusCode() +
                            ". Error message: " + response.body());
                    if (retry++ < 5) {
                        LOGGER.error("Trying again in 10 seconds...");
                        Thread.sleep(10000);
                        return this.get(url, typeReference);
                    }
                }
            } else {
                LOGGER.info("Using cached data in: " + filePath);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(new File(filePath), typeReference);
            }
        } catch (JsonEOFException e) {
            LOGGER.error("JSON unexpected end-of-input. Verify json file of " + url);
        } catch (IOException e) {
            LOGGER.error("Error writing data in json file");
        } catch (InterruptedException e) {
            LOGGER.error("Error feting data: " + e.getMessage());
        }

        return null;
    }

    public OpenDataBaseSingleResponse<T> get(TypeReference<OpenDataBaseSingleResponse<T>> typeReference, String url) {
        try {
            UriParams uriParams = extractUri(url);
            String filePath = getFilePath(uriParams);

            if (!FileUtils.isFileAlreadyCreated(filePath)) {
                LOGGER.info("Sending GET request: " + url);
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    saveResponseInLocalCache(url, response.body(), false);

                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(response.body(), typeReference);
                } else {
                    LOGGER.error("Error sending request. Status code: " + response.statusCode() +
                            ". Error message: " + response.body());
                }
            } else {
                LOGGER.info("Using cached data in: " + filePath);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(new File(filePath), typeReference);
            }
        } catch (JsonEOFException e) {
            LOGGER.error("JSON unexpected end-of-input. Verify json file of " + url);
        } catch (IOException e) {
            LOGGER.error("Error writing data in json file");
        } catch (InterruptedException e) {
            LOGGER.error("Error feting data: " + e.getMessage());
        }

        return null;
    }

    public OpenDataBaseResponseList<T> get(String url, TypeReference<OpenDataBaseResponseList<T>> typeReference, boolean useCache) {
        try {
            UriParams uriParams = extractUri(url);
            String filePath = getFilePath(uriParams);

            if (!FileUtils.isFileAlreadyCreated(filePath) && !useCache) {
                LOGGER.info("Sending GET request: " + url);
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    saveResponseInLocalCache(url, response.body(), true);

                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(response.body(), typeReference);
                } else {
                    LOGGER.error("Error sending request. Status code: " + response.statusCode() +
                            ". Error message: " + response.body());
                }
            } else {
                LOGGER.info("Using cached data in: " + filePath);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(new File(filePath), typeReference);
            }
        } catch (JsonEOFException e) {
            LOGGER.error("JSON unexpected end-of-input. Verify json file of " + url);
        } catch (IOException e) {
            LOGGER.error("Error writing data in json file. Error: " + e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("Error feting data: " + e.getMessage());
        }

        return null;
    }


    public OpenDataBaseResponseList<T> getJson(String url, TypeReference<OpenDataBaseResponseList<T>> typeReference) {
        try {
            // https://dadosabertos.camara.leg.br/arquivos/votacoes/json/votacoes-2023.json
            int lastBarIndex = url.lastIndexOf('/');
            String fileName = url.substring(lastBarIndex + 1);

            int dashIndex = fileName.indexOf('-');
            String directoryName = fileName.substring(0, dashIndex);

            String filePath = ResourceUtils.RESOURCE_PATH + "/" + directoryName + "/" + fileName;

            if (!FileUtils.isFileAlreadyCreated(filePath)) {
                LOGGER.info("Sending GET request: " + url);
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    saveJsonInLocalCache(filePath, response.body(), true);

                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(response.body(), typeReference);
                } else {
                    LOGGER.error("Error sending request. Status code: " + response.statusCode() +
                            ". Error message: " + response.body());
                }
            } else {
                LOGGER.info("Using cached data in: " + filePath);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(new File(filePath), typeReference);
            }
        } catch (JsonEOFException e) {
            LOGGER.error("JSON unexpected end-of-input. Verify json file of " + url);
        } catch (IOException e) {
            LOGGER.error("Error writing data in json file");
        } catch (InterruptedException e) {
            LOGGER.error("Error feting data: " + e.getMessage());
        }

        return null;
    }

    private void saveResponseInLocalCache(String url, String jsonResponseBody, boolean isList) {
        try {
            UriParams uriParams = extractUri(url);
            File file = getFile(uriParams);

            // Save in cache only if response has some data
            if (!jsonResponseBody.contains("\"dados\":[]")) {
                if (isList) {
                    FileUtils.saveFileResultList(file.getPath(), jsonResponseBody);
                } else {
                    FileUtils.saveFileSingleResult(file.getPath(), jsonResponseBody);
                }
                LOGGER.info("JSON saved in: " + file.getAbsolutePath());
            } else {
                LOGGER.warn("No data to be saved in cache.");
            }
        } catch (IndexOutOfBoundsException e) {
            LOGGER.error("Error saving response in local cache: Index out bounds exception in splitUri");
        }
    }

    private void saveJsonInLocalCache(String filePath, String jsonResponseBody, boolean isList) {
        try {
            File file = FileUtils.createFile(filePath);

            // Save in cache only if response has some data
            if (!jsonResponseBody.contains("\"dados\":[]")) {
                if (isList) {
                    FileUtils.saveFileResultList(file.getPath(), jsonResponseBody);
                } else {
                    FileUtils.saveFileSingleResult(file.getPath(), jsonResponseBody);
                }
                LOGGER.info("JSON saved in: " + file.getAbsolutePath());
            } else {
                LOGGER.warn("No data to be saved in cache.");
            }
        } catch (IndexOutOfBoundsException e) {
            LOGGER.error("Error saving response in local cache: Index out bounds exception in splitUri");
        }
    }

    private String getFilePath(UriParams uriParams) {
        String resourcesPath = ResourceUtils.RESOURCE_PATH;
        String filePath;

        if (uriParams.splitUri.length == 1) {
            String fileName = uriParams.splitUri[0];
            filePath = resourcesPath + "/" + uriParams.splitUri[0] + "/" + fileName + ".json";
        } else if (uriParams.splitUri.length == 3) {
            String fileName = uriParams.splitUri[1]; // id property
            filePath = resourcesPath + "/" + uriParams.splitUri[0] + "/" + uriParams.splitUri[2] + "/" + fileName + ".json";
        } else {
            String fileName = uriParams.splitUri[1]; // id property
            filePath = resourcesPath + "/" + "/" + uriParams.splitUri[0] + "/" + fileName + ".json";
        }

        return filePath;
    }

    private File getFile(UriParams uriParams) {
        String filePath = getFilePath(uriParams);
        return FileUtils.createFile(filePath);
    }

    private UriParams extractUri(String url) {
        UriParams uriParams = new UriParams();

        int initialIndex = 42;
        int finalIndex = url.length();

        if (url.contains("?")) {
            finalIndex = url.indexOf('?');
        }

        String resourcePath = url.substring(initialIndex, finalIndex);
        String[] params = resourcePath.split("/");

        uriParams.setSplitUri(params);
        uriParams.setUri(resourcePath);

        return uriParams;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class UriParams {
        private String uri;
        private String[] splitUri;
    }
}
