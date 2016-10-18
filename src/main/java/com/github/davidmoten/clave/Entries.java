package com.github.davidmoten.clave;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Entries {

    @XmlElement(name = "entry")
    public List<Entry> entries;

    public Entries() {
        entries = new ArrayList<>();
    }

    public Entries(List<Entry> entries) {
        this.entries = entries;
    }

}
