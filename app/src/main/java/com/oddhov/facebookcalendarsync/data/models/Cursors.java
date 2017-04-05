
package com.oddhov.facebookcalendarsync.data.models;

import java.util.HashMap;
import java.util.Map;

class Cursors {

    private String before;
    private String after;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
