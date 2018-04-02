package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class UserSeq extends PgSequenceGenerator {

    public UserSeq() {
        this.setSequence("pg_adm_user_id_seq");
    }
}
