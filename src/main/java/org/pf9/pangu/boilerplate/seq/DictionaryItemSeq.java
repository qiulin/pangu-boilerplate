package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class DictionaryItemSeq extends PgSequenceGenerator {

    public DictionaryItemSeq() {

        this.setSequence("he_adm_dictionary_item_seq");
    }
}
