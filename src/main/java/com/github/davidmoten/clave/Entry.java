package com.github.davidmoten.clave;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.github.davidmoten.clave.Entry.EntryAdapter;
import com.github.davidmoten.clave.EntryMutable.AttachmentMetadataMutable;

@XmlJavaTypeAdapter(EntryAdapter.class)
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class Entry {

    final String id;
    final String name;
    final String username;
    // corresponds to the name of the ZipEntry containing
    final String passwordId;
    final String passwordSalt;
    final String notes;
    final String url;
    final Set<String> tags;
    final List<AttachmentMetadata> attachments;

    public Entry(String id, String name, String username, String passwordId, String passwordSalt,
            String notes, String url, Set<String> tags, List<AttachmentMetadata> attachments) {
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

    public static class AttachmentMetadata {
        final String id;
        final int size;

        AttachmentMetadata(String id, int size) {
            this.id = id;
            this.size = size;
        }
    }

    public static class EntryAdapter extends XmlAdapter<EntryMutable, Entry> {

        @Override
        public Entry unmarshal(EntryMutable v) throws Exception {
            return new Entry(v.getId(), v.getName(), v.getUsername(), v.getPasswordId(),
                    v.getPasswordSalt(), v.getNotes(), v.getUrl(), v.getTags(),
                    v.getAttachments().stream().map(a -> toAttachmentMutable(a))
                            .collect(Collectors.toList()));
        }

        private static AttachmentMetadata toAttachmentMutable(AttachmentMetadataMutable a) {
            return new AttachmentMetadata(a.getId(), a.getSize());
        }

        @Override
        public EntryMutable marshal(Entry v) throws Exception {
            return new EntryMutable(v.id, v.name, v.username, v.passwordId, v.passwordSalt, v.notes,
                    v.url, v.tags,
                    v.attachments.stream().map(a -> toAttachment(a)).collect(Collectors.toList()));
        }

        private AttachmentMetadataMutable toAttachment(AttachmentMetadata a) {
            return new AttachmentMetadataMutable(a.id, a.size);
        }

    }

}
