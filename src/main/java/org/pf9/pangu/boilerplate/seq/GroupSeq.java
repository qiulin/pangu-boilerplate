package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class GroupSeq extends PgSequenceGenerator{
    public GroupSeq() {
        this.setSequence("he_adm_group_seq");
    }
}
