package com.github.davidmoten.clave;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import com.github.davidmoten.clave.Entry.AttachmentMetadata;
import com.github.davidmoten.guavamini.Lists;
import com.github.davidmoten.guavamini.Sets;

public class EntryTest {

    @Test
    public void testMarshal() throws JAXBException {
        Entry entry = new Entry("123", "name", "username", "passwordId", "passwordSalt", "notes",
                "url", Sets.newHashSet("tag1", "tag2"),
                Lists.newArrayList(new AttachmentMetadata("456", 880)));

        Entries entries = new Entries(Lists.newArrayList(entry));

        JAXBContext context = JAXBContext.newInstance(Entries.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        m.marshal(entries, bytes);
        System.out.println(bytes.toString());
    }

}
