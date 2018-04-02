package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class DictionarySeq extends PgSequenceGenerator {

    public DictionarySeq() {
        this.setSequence("pg_adm_dictionary_id_seq");
    }
}
