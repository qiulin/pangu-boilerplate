package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class GroupSeq extends PgSequenceGenerator{
    public GroupSeq() {
        this.setSequence("pg_adm_group_id_seq");
    }
}
