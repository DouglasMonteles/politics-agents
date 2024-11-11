package org.fga.tcc.json;

import java.io.File;

public class RouterManager {

    private StringBuilder url;

    private final String separator = File.separator;

    public RouterManager setUrl(String url) {
        this.url = new StringBuilder(url);
        return this;
    }

    public String getUrl() {
        return this.url.toString();
    }

    public RouterManager setRequestUri(String uri) {
        this.url.append(separator);
        this.url.append(uri);
        return this;
    }

    public RouterManager setRequestParamId(Integer id) {
        this.url.append(separator);
        this.url.append(id);
        return this;
    }

    public RouterManager setRequestParamId(String id) {
        this.url.append(separator);
        this.url.append(id);
        return this;
    }

    public RouterManager setPage(Integer page) {
        this.addSpecialCharacters();

        this.url.append("pagina=");
        this.url.append(page);
        return this;
    }

    public RouterManager setOrder(String order) {
        this.addSpecialCharacters();

        this.url.append("ordem=");
        this.url.append(order);
        return this;
    }

    public RouterManager setOrderBy(String orderBy) {
        this.addSpecialCharacters();

        this.url.append("ordenarPor=");
        this.url.append(orderBy);
        return this;
    }

    public RouterManager setItems(Integer quantity) {
        this.addSpecialCharacters();

        this.url.append("itens=");
        this.url.append(quantity);
        return this;
    }

    /*
     * @param initialDate AAAA-MM-DD
     * @param finalDate AAAA-MM-DD
     * */
    public RouterManager setInterval(String initialDate, String finalDate) {
        this.addSpecialCharacters();

        this.url.append("dataInicio=");
        this.url.append(initialDate);

        this.url.append("&");

        this.url.append("dataFim=");
        this.url.append(finalDate);

        return this;
    }

    /*
    * @param jsonName Name of json without .json
    * */
    public RouterManager setJsonName(String jsonName) {
        this.url.append(jsonName);
        this.url.append(".json");
        return this;
    }

    private void addSpecialCharacters() {
        // In case of this param to be the first
        if (this.url.indexOf("?") == -1) {
            this.url.append("?");
        }

        // In case of this query param not to be the first
        if (this.url.indexOf("=") != -1) {
            this.url.append("&");
        }
    }

}
