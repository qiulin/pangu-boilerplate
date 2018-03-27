package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderSeq extends PgSequenceGenerator {

    public WorkOrderSeq(){
        this.setSequence("he_adm_workorder_seq");
    }
}
