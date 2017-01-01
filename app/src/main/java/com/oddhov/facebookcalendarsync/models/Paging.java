
package com.oddhov.facebookcalendarsync.models;

import java.util.HashMap;
import java.util.Map;

class Paging {

    private Cursors cursors;
    private String next;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public Cursors getCursors() {
        return cursors;
    }

    public void setCursors(Cursors cursors) {
        this.cursors = cursors;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
