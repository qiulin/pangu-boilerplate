package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class MenuSeq extends PgSequenceGenerator {

    public MenuSeq() {
        this.setSequence("pg_adm_menu_id_seq");
    }
}
