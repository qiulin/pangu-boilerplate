package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class DictionaryItemSeq extends PgSequenceGenerator {

    public DictionaryItemSeq() {

        this.setSequence("pg_adm_dictionary_item_id_seq");
    }
}
