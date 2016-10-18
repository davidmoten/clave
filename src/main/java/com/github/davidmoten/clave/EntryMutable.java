package com.github.davidmoten.clave;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class EntryMutable {

    private String id;
    private String name;
    private String username;
    // corresponds to the name of the ZipEntry containing the password bytes
    private String passwordId;
    private String passwordSalt;
    private String notes;
    private String url;
    private Set<String> tags;
    private List<AttachmentMetadataMutable> attachments;

    public EntryMutable(String id, String name, String username, String passwordId,
            String passwordSalt, String notes, String url, Set<String> tags,
            List<AttachmentMetadataMutable> attachments) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.passwordId = passwordId;
        this.passwordSalt = passwordSalt;
        this.notes = notes;
        this.url = url;
        this.tags = tags;
        this.attachments = attachments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordId() {
        return passwordId;
    }

    public void setPasswordId(String passwordId) {
        this.passwordId = passwordId;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlElement(name = "tag")
    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @XmlElement(name = "attachment")
    public List<AttachmentMetadataMutable> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentMetadataMutable> attachments) {
        this.attachments = attachments;
    }

    public static final class AttachmentMetadataMutable {

        private String id;
        private int size;

        AttachmentMetadataMutable(String id, int size) {
            this.id = id;
            this.size = size;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

    }

}
